package ru.prolib.aquila.utils.experimental.swing_chart;

import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.DefaultLabelFormatter;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.LabelFormatter;
import ru.prolib.aquila.utils.experimental.swing_chart.layers.ChartLayer;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.prolib.aquila.utils.experimental.swing_chart.ChartConstants.OTHER_CHARTS_HEIGHT;
import static ru.prolib.aquila.utils.experimental.swing_chart.ChartConstants.TOOLTIP_MARGIN;

/**
 * Created by TiM on 18.06.2017.
 */
public class ChartPanel<TCategories> implements MouseWheelListener, MouseMotionListener {

    protected Map<String, Chart<TCategories>> charts = new LinkedHashMap<>();
    protected final List<TCategories> categories = new ArrayList<>();
    protected final List<TCategories> displayedCategories = new ArrayList<>();
    protected AtomicInteger currentPosition = new AtomicInteger(0);
    protected AtomicInteger numberOfPoints = new AtomicInteger(40);

    protected JPanel mainPanel;
    protected JScrollBar scrollBar;
    protected AdjustmentListener scrollBarListener;
    protected int lastX, lastY;
    protected TooltipForm tooltipForm;
    protected LabelFormatter categoryLabelFormatter = new DefaultLabelFormatter();
    protected LabelFormatter valueLabelFormatter = new DefaultLabelFormatter();
    private Rectangle screen;
    private final Timer updateTooltipTextTimer;
    private final HashMap<JPanel, Chart<TCategories>> chartByPanel = new HashMap<>();
    private final JPanel rootPanel;

    public ChartPanel() {
        super();
        rootPanel = new JPanel(new BorderLayout());
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS) );
        rootPanel.add(mainPanel, BorderLayout.CENTER);
        scrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
        scrollBarListener = new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                setCurrentPosition(e.getValue());
            }
        };
        scrollBar.addAdjustmentListener(scrollBarListener);
        rootPanel.add(scrollBar, BorderLayout.SOUTH);
        tooltipForm = new TooltipForm();
        screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();

        updateTooltipTextTimer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTooltipText_();
            }
        });
        updateTooltipTextTimer.setRepeats(false);
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public Chart<TCategories> addChart(String id){
        return addChart(id, null);
    }

    public Chart<TCategories> addChart(String id, Integer height) throws IllegalArgumentException {
        if(charts.containsKey(id)){
            throw new IllegalArgumentException("Chart with id='"+id+"' already added");
        }
        Chart<TCategories> chart = new Chart<>(displayedCategories);
        if(charts.size()==0) {
            chart.getRootPanel().setPreferredSize(new Dimension(rootPanel.getWidth(), rootPanel.getHeight()));
        } else {
            chart.getRootPanel().setMaximumSize(new Dimension(2000, height==null?OTHER_CHARTS_HEIGHT:height));
            chart.getRootPanel().setMinimumSize(new Dimension(100, height==null?OTHER_CHARTS_HEIGHT:height));
            chart.getRootPanel().setPreferredSize(new Dimension(rootPanel.getWidth(), height==null?OTHER_CHARTS_HEIGHT:height));
        }
        charts.put(id, chart);
        mainPanel.add(chart.getRootPanel());
        chart.getRootPanel().addMouseWheelListener(this);
        chart.getRootPanel().addMouseMotionListener(this);
        chart.getRootPanel().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                tooltipForm.setVisible(false);
            }
        });
        updateLabelsConfig();
        chartByPanel.put(chart.getRootPanel(), chart);
        getRootPanel().validate();
        getRootPanel().repaint();
        return chart;
    }

    public Chart<TCategories> getChart(String id){
        return charts.get(id);
    }

    public ChartLayer<TCategories, ?> addLayer(String chartId, ChartLayer<TCategories, ?> layer){
        Chart<TCategories> chart = getChart(chartId);
        if (chart == null) {
            chart = addChart(chartId);
        }
        ChartLayer<TCategories, ?> l = chart.getLayer(layer.getId());
        if(l!=null){
            throw new IllegalArgumentException(String.format("Layer with id=%s already exists", layer.getId()));
        }
        chart.addLayer(layer);
        return layer;
    }

    public ChartLayer<TCategories, ?> getLayer(String chartId, String layerId) {
        Chart<TCategories> chart = getChart(chartId);
        ChartLayer<TCategories, ?> layer = null;
        if(chart!=null){
            return chart.getLayer(layerId);
        }
        return null;
    }

    public void setCurrentPosition(int position){
        setCurrentPosition(position, false);
    }

    public void setCurrentPosition(int position, boolean newCategoryAdded){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if(position < 0){
                    currentPosition.set(0);
                } else if(!newCategoryAdded && categories.size()>0 && position > categories.size()-getNumberOfPoints()){
                    currentPosition.set(categories.size()-getNumberOfPoints());
                } else {
                    currentPosition.set(position);
                }
                updateCategories();
                repaintCharts();
                updateScrollbarAndSetValue();
                updateTooltipText();
            }
        });
    }

    public int getCurrentPosition() {
        return currentPosition.get();
    }

    public int getNumberOfPoints() {
        return numberOfPoints.get();
    }

    public void setNumberOfPoints(int numberOfPoints) {
        if(numberOfPoints<2){
            numberOfPoints = 2;
        } else if(numberOfPoints > categories.size()){
            numberOfPoints = categories.size();
        }
        this.numberOfPoints.set(numberOfPoints);
        setCurrentPosition(getCurrentPosition());
    }

    public void clearData(){
        for(Chart<TCategories> c: charts.values()){
            for(ChartLayer<TCategories, ?> l: c.getLayers()){
                l.clearData();
            }
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if(e.isControlDown()){
            setNumberOfPoints(getNumberOfPoints()+e.getWheelRotation());
        } else {
            setCurrentPosition(getCurrentPosition()+e.getWheelRotation());
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
//        System.out.printf("x=%d; y=%d%n", e.getX(), e.getY());
        setLastX(e.getX());
        setLastY(e.getY());
        repaintCharts();

        Chart<TCategories> chart = chartByPanel.get(e.getSource());
        if(chart!=null){
            TCategories category = chart.getLastCategory();
            if(category==null){
                tooltipForm.setVisible(false);
            } else {
                updateTooltipText(category);
                Point point = chart.getRootPanel().getLocationOnScreen();
                int x = point.x + lastX;
                int y = point.y + lastY;
                if(x + tooltipForm.getWidth() <= screen.getMaxX()){
                    x = x+TOOLTIP_MARGIN;
                } else {
                    x = x - tooltipForm.getWidth()-TOOLTIP_MARGIN;
                }
                if(y + tooltipForm.getHeight() <= screen.getMaxY()){
                    y = y+TOOLTIP_MARGIN;
                } else {
                    y = y - tooltipForm.getHeight()-TOOLTIP_MARGIN;
                }
                tooltipForm.setLocation(x, y);
                tooltipForm.setVisible(true);
            }
        }
    }

    public int getLastX() {
        return lastX;
    }

    public void setLastX(int lastX) {
        this.lastX = lastX;
        for(Chart c: charts.values()){
            c.setLastX(lastX);
        }
    }

    public int getLastY() {
        return lastY;
    }

    public void setLastY(int lastY) {
        this.lastY = lastY;
        repaintCharts();
    }

    public List<TCategories> getDisplayedCategories() {
        return displayedCategories;
    }

    public List<TCategories> getCategories() {
        return categories;
    }

    protected boolean isCategoryDisplayed(TCategories category){
        return displayedCategories.contains(category);
    }

    private void updateTooltipText(){
        if(!updateTooltipTextTimer.isRunning()){
            updateTooltipTextTimer.start();
        }
    }

    private void updateTooltipText_(){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                for(Chart<TCategories> c: charts.values()){
                    TCategories category = c.getLastCategory();
                    if(category!=null){
                        updateTooltipText(category);
                    }
                    return;
                }
            }
        });
    }

    private void updateTooltipText(TCategories category){
        StringBuilder sb = new StringBuilder();
        for(Chart<TCategories> c: charts.values()){
            for(ChartLayer<TCategories, ?> l: c.getLayers()){
                String txt = l.getTooltip(category);
                if(txt!=null){
                    if(sb.length()!=0){
                        sb.append("\n----------\n");
                    }
                    sb.append(txt);
                }
            }
        }
        String txt = sb.toString();
        txt = txt.replace("\n----------\n", "<hr>").replace("\n", "<br>");
        tooltipForm.setText("<html>"+txt+"</html>");
    }

    protected void updateCategories() {
        if(!SwingUtilities.isEventDispatchThread()){
            throw new IllegalStateException("It should be called from AWT Event queue thread");
        }
        Set<TCategories> set = new TreeSet<>();

        for(Chart<TCategories> chart: charts.values()){
            for (ChartLayer<TCategories, ?> layer : chart.getLayers()) {
                Series<TCategories> c = layer.getCategories();
                if(c!=null){
                    for (int i = 0; i < c.getLength(); i++) {
                        try {
                            set.add(c.get(i));
                        } catch (ValueException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        categories.clear();
        categories.addAll(set);
        displayedCategories.clear();
        for(int i=0; i<categories.size(); i++){
            if(i >= getCurrentPosition() && i < getCurrentPosition() + getNumberOfPoints()){
                displayedCategories.add(categories.get(i));
            }
        }
    }

    protected void updateScrollbar(int countCategories){
        if (scrollBar != null) {
            scrollBar.setMinimum(0);
            scrollBar.setMaximum(countCategories);
            scrollBar.setVisibleAmount(getNumberOfPoints());
        }
    }

    private void updateScrollbar() {
        updateScrollbar(categories.size());
    }

    private void updateScrollbarAndSetValue() {
        if (scrollBar != null) {
            scrollBar.removeAdjustmentListener(scrollBarListener);
            updateScrollbar();
            scrollBar.setValue(getCurrentPosition());
            scrollBar.addAdjustmentListener(scrollBarListener);
        }
    }

    private void repaintCharts(){
        if(charts!=null){
            for(Chart chart: charts.values()){
                chart.getRootPanel().repaint();
            }
        }
    }

    protected void updateLabelsConfig(){
        int i=0;
        for(Chart c: charts.values()){
            c.getTopAxis().setShowLabels(false);
            c.getBottomAxis().setShowLabels(false);
            if(i==0){
                c.getTopAxis().setLabelOrientation(SwingConstants.HORIZONTAL);
                c.getTopAxis().setLabelFormatter(categoryLabelFormatter);
                c.getTopAxis().setShowLabels(true);
                c.getBottomAxis().setShowLabels(false);
            }
            if(i==charts.size()-1){
                c.getBottomAxis().setLabelOrientation(SwingConstants.VERTICAL);
                c.getBottomAxis().setLabelFormatter(categoryLabelFormatter);
                c.getBottomAxis().setShowLabels(true);
            }
            c.getLeftAxis().setShowLabels(true);
            c.getRightAxis().setShowLabels(true);
            if(c.getValuesLabelFormatter().getClass().equals(DefaultLabelFormatter.class)){
                c.setValuesLabelFormatter(valueLabelFormatter);
            }
            i++;
        }
    }
}

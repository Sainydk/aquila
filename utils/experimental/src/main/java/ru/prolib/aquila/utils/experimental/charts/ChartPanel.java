package ru.prolib.aquila.utils.experimental.charts;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.tuple.Pair;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.utils.experimental.charts.formatters.CategoriesLabelFormatter;
import ru.prolib.aquila.utils.experimental.charts.layers.ChartLayer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

/**
 * Created by TiM on 22.12.2016.
 */
public class ChartPanel<T> extends JFXPanel {

    public static final String CURRENT_POSITION_CHANGE="CURRENT_POSITION_CHANGE";
    public static final String NUMBER_OF_POINTS_CHANGE="NUMBER_OF_POINTS_CHANGE";
    private ActionListener actionListener;

    private final BorderPane borderPane;
    private final VBox mainPanel;
    private final HashMap<String, Chart> charts = new LinkedHashMap<>();
    private final HashMap<String, List<ChartLayer>> chartLayers = new HashMap<>();
    private final List<T> categories = new ArrayList<>();
    private CategoriesLabelFormatter<T> categoriesLabelFormatter;
    private int currentPosition = 0;
    private int numberOfPoints = 15;
    private ScrollBar scrollBar;
    private ChangeListener<Number> scrollBarListener;

    private boolean fullRedraw = true;

    public ChartPanel() {
        super();
        mainPanel = new VBox();
        borderPane = new BorderPane(mainPanel);

        this.setScene(new Scene(borderPane));

        this.getScene().widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> refresh());
        this.getScene().heightProperty().addListener((observableValue, oldSceneHeight, newSceneHeight) -> refresh());

    }

    public void addChart(String id){
        Chart<T> chart = new Chart<T>(new NumberAxis(), new NumberAxis());
        chart.setOnMouseClicked(event->{

        });
        chart.setOnScroll(event -> {
            if (event.isControlDown()) {
                setNumberOfPoints(getNumberOfPoints() - (int) Math.signum(event.getDeltaY()));
            } else {
                setCurrentPosition(getCurrentPosition() - (int) Math.signum(event.getDeltaY()));
            }
        });
        chart.setCategoriesLabelFormatter(categoriesLabelFormatter);
        charts.put(id, chart);
        chartLayers.put(id, new ArrayList<>());

        Platform.runLater(()->{
            mainPanel.getChildren().add(chart);
            if(charts.size()==1){
                VBox.setVgrow(chart, Priority.ALWAYS);
            }
        });
    }

    public List<T> getCategories(){
        return categories;
    }

    public Integer getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        updateCategories();
        if(categories.size()==0){
            return;
        }
        if (currentPosition < 0) {
            setCurrentPosition(0);
        } else if (currentPosition > 0 && currentPosition > categories.size() - numberOfPoints) {
            setCurrentPosition(categories.size() - numberOfPoints);
        } else {
            this.currentPosition = currentPosition;
            refresh();
            updateScrollbarAndSetValue();
            if(actionListener!=null){
                actionListener.actionPerformed(new ActionEvent(this, 1, CURRENT_POSITION_CHANGE));
            }
        }
    }

    public Integer getNumberOfPoints() {
        return numberOfPoints;
    }

    public void setNumberOfPoints(int numberOfPoints) {
        if (numberOfPoints < 2) {
            numberOfPoints = 2;
        }
        if (numberOfPoints > categories.size()) {
            numberOfPoints = categories.size();
        }
        this.numberOfPoints = numberOfPoints;
        updateScrollbar();
        if(actionListener!=null){
            actionListener.actionPerformed(new ActionEvent(this, 1, NUMBER_OF_POINTS_CHANGE));
        }
        setCurrentPosition(currentPosition);
    }

    boolean started = false;
    Timer timerUpdate = new Timer();

    public void refresh(){
//        System.out.println("REFRESH NEEDED");
        if(!started){
            started = true;
            TimerTask timerUpdateTask = new TimerTask() {
                @Override
                public void run() {
//                    System.out.println("REFRESH PROCESSING");
                    started = false;
                    _refresh();
                }
            };
            timerUpdate.schedule(timerUpdateTask, 100);
        }
    }

    private void _refresh(){
        Platform.runLater(()->{
            setAxisValues();
            for(String id: charts.keySet()){
                Chart chart = getChart(id);
                if(fullRedraw){
                    chart.clearPlotChildren();
                }
                chart.clearObjectBounds();
                chart.updatePlotChildren(paintChartObjects(id));
            }
            fullRedraw = false;
        });
    }

    public void addChartLayer(String chartId, ChartLayer object){
        object.setChart(getChart(chartId));
        getChartLayers(chartId).add(object);
    }

    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void setCategoriesLabelFormatter(CategoriesLabelFormatter formatter) {
        this.categoriesLabelFormatter = formatter;
        for(Chart chart: charts.values()){
            chart.setCategoriesLabelFormatter(formatter);
        }
    }

    public boolean isCategoryDisplayed(T category){
        for(Chart chart: charts.values()){
            if(chart.isCategoryDisplayed(category)){
                return true;
            }
        }
        return false;
    }

    private void updateCategories(){
        Set<T> set = new TreeSet<>();

        categories.clear();
        for(List<ChartLayer> objects: chartLayers.values()){
            for(ChartLayer obj: objects){
                Series<T> c = obj.getCategories();
                for(int i=0; i< c.getLength(); i++){
                    try {
                        set.add(c.get(i));
                    } catch (ValueException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        categories.addAll(set);
    }

    private void setAxisValues(){
        List<T> categoriesToDisplay = new ArrayList<>();
        int cnt = Math.min(currentPosition + numberOfPoints, categories.size());
        for(int i=currentPosition; i<cnt; i++){
            categoriesToDisplay.add(categories.get(i));
        }

        for(String id: charts.keySet()){
            Chart chart = getChart(id);
            double maxY = 0;
            double minY = 1e6;
            for(ChartLayer obj: getChartLayers(id)){
                Pair<Double, Double> interval = obj.getValuesInterval(categoriesToDisplay);
                if(interval!=null){
                    if(interval.getRight()!=null && interval.getRight() > maxY){
                        maxY = interval.getRight();
                    }
                    if(interval.getLeft()!=null && interval.getLeft() < minY){
                        minY = interval.getLeft();
                    }
                }
            }
            chart.setAxisValues(categoriesToDisplay, minY, maxY);
        }
    }

    private List<Node> paintChartObjects(String id){
        List<Node> result = new ArrayList<>();
        for(ChartLayer obj: getChartLayers(id)){
            result.addAll(obj.paint());
        }
        return result;
    }

    public Chart getChart(String chartId) {
        Chart chart = charts.get(chartId);
        if(chart==null){
            throw new IllegalArgumentException("Unknown chart with id = "+chartId);
        }
        return chart;
    }

    public List<ChartLayer> getChartLayers(String chartId) {
        List<ChartLayer> objects = chartLayers.get(chartId);
        if(objects==null){
            throw new IllegalArgumentException("Unknown chart with id = "+chartId);
        }
        return objects;
    }

    public void setFullRedraw(boolean fullRedraw) {
        this.fullRedraw = fullRedraw;
    }

    public void setScrollbar(ScrollBar scrollbar){
        if(this.scrollBar==null){
            this.scrollBar = scrollbar;
            scrollBarListener = new ChangeListener<Number>(){
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    int position = (int)Math.round(newValue.doubleValue());
                    setCurrentPosition(position);
                }
            };

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    if(scrollbar.getOrientation().equals(Orientation.HORIZONTAL)){
                        borderPane.setBottom(scrollbar);
                    } else {
                        borderPane.setRight(scrollbar);
                    }
                    updateScrollbar();
                    scrollbar.valueProperty().addListener(scrollBarListener);
                }
            });
        } else {
            throw new IllegalStateException("Scrollbar already attached");
        }
    }

    private void updateScrollbar(){
        if(scrollBar!=null){
            scrollBar.setMin(0);
            double max = getCategories().size() - getNumberOfPoints();
            double vis = getNumberOfPoints() * max / getCategories().size();
            scrollBar.setMax(max);
            scrollBar.setVisibleAmount(vis);
            scrollBar.setUnitIncrement(1);
            scrollBar.setBlockIncrement(getNumberOfPoints());
        }
    }

    private void updateScrollbarAndSetValue(){
        if(scrollBar!=null){
            scrollBar.valueProperty().removeListener(scrollBarListener);
            updateScrollbar();
            scrollBar.setValue(getCurrentPosition());
            scrollBar.valueProperty().addListener(scrollBarListener);
        }
    }
}


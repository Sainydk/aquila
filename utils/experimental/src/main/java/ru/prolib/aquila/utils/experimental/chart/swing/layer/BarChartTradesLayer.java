package ru.prolib.aquila.utils.experimental.chart.swing.layer;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.core.utils.Range;
import ru.prolib.aquila.utils.experimental.chart.BCDisplayContext;
import ru.prolib.aquila.utils.experimental.chart.BarChartLayer;

import java.awt.*;
import java.util.List;

import static ru.prolib.aquila.utils.experimental.chart.ChartConstants.TRADE_BUY_COLOR;
import static ru.prolib.aquila.utils.experimental.chart.ChartConstants.TRADE_LINE_COLOR;
import static ru.prolib.aquila.utils.experimental.chart.ChartConstants.TRADE_SELL_COLOR;

/**
 * Created by TiM on 16.09.2017.
 */
public class BarChartTradesLayer extends BarChartAbstractLayer {

    public static final int ACCOUNTS_PARAM = 0;

    public static final int LINE_COLOR = 0;
    public static final int BUY_COLOR = 1;
    public static final int SELL_COLOR = -1;

    private final int HEIGHT = 10;
    private final int WIDTH = 20;

    private List<Account> accounts;
    private Series<TradeInfoList> data;

    public BarChartTradesLayer(Series<TradeInfoList> data) {
        super(data.getId());
        this.data = data;
        colors.put(LINE_COLOR, TRADE_LINE_COLOR);
        colors.put(BUY_COLOR, TRADE_BUY_COLOR);
        colors.put(SELL_COLOR, TRADE_SELL_COLOR);
    }
    
    @Override
    public Range<CDecimal> getValueRange(int first, int number) {
    	// TODO: fixme, I have this range
    	return null;
    }
    
    @Override
    protected void paintLayer(BCDisplayContext context, Graphics2D graphics) {
    	
    }

    /*
    @Override
    protected void paintObject(int categoryIdx, TradeInfoList tradeInfoList, BarChartVisualizationContext context, Graphics2D g) {
        if (tradeInfoList != null) {
            tradeInfoList = new TradeInfoList(tradeInfoList, accounts);
            Color color = colors.get(LINE_COLOR);
            int x = context.toCanvasX(categoryIdx);
            for (int j = 0; j < tradeInfoList.size(); j++) {
                TradeInfo tradeInfo = tradeInfoList.get(j);
                int y = context.toCanvasY(tradeInfo.getPrice());
                int y2;
                Color fillColor;
                if (tradeInfo.getAction().equals(OrderAction.BUY)) {
                    y2 = y + HEIGHT;
                    fillColor = colors.get(BUY_COLOR);
                } else {
                    y2 = y - HEIGHT;
                    fillColor = colors.get(SELL_COLOR);
                }
                int[] xPoints = {x, x - WIDTH / 2, x + WIDTH / 2};
                int[] yPoints = {y, y2, y2};
                g.setColor(color);
                g.drawPolygon(xPoints, yPoints, 3);
                g.setColor(fillColor);
                g.fillPolygon(xPoints, yPoints, 3);
            }
        }
    }

    @Override
    public Range<CDecimal> getValueRange(int first, int number) {
        CDecimal minY = CDecimalBD.ZERO;
        CDecimal maxY = CDecimalBD.ZERO;
        if ( ! visible || data == null ) {
            return null;
        }
        data.lock();
        try {
            for(int i=first; i< first + number; i++){
                TradeInfoList value = null;
                try {
                    value = data.get(i);
                } catch (ValueException e) {
                    value = null;
                }
                if(value!=null) {
                    value = new TradeInfoList(value, accounts);
                }
                if(value!=null && value.size()>0){
                    maxY = maxY.max(getMaxValue(value));
                    minY = minY.min(getMinValue(value));
                }
            }
        } finally {
            data.unlock();
        }
        if(minY!=null && maxY!=null){
            return Range.between(minY, maxY);
        }
        return null;
    }

    @Override
    protected CDecimal getMaxValue(TradeInfoList value) {
    	CDecimal x = value.getMaxValue();
        return x == null ? CDecimalBD.ZERO : x;
    }

    @Override
    protected CDecimal getMinValue(TradeInfoList value) {
    	CDecimal x = value.getMinValue();
        return x == null ? CDecimalBD.ZERO : x;
    }

    @Override
    protected String createTooltipText(TradeInfoList value, LabelFormatter labelFormatter) {
        value = new TradeInfoList(value, accounts);
        if(value.size()>0){
            StringBuilder sb = new StringBuilder("Orders:\n");
            for(int i=0; i< value.size(); i++){
                TradeInfo ti = value.get(i);
                sb.append(ti.getOrderId());
                sb.append(";\n");
            }
            return sb.toString();
        }
        return null;
    }

    @Override
    public BarChartLayer<TCategory> setParam(int paramId, Object value) {
        super.setParam(paramId, value);
        if(paramId == ACCOUNTS_PARAM && value instanceof List){
            accounts = (List<Account>) value;
        }
        return this;
    }
    */
}
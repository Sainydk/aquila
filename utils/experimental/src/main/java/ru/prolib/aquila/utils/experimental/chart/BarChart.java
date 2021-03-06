package ru.prolib.aquila.utils.experimental.chart;

import java.util.List;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDriverProxy;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDriver;

/**
 * Created by TiM on 08.09.2017.
 */
public interface BarChart {
	
	@Deprecated
	BarChart setHeight(int points);
	@Deprecated
	int getHeight();
	
	CategoryAxisDriverProxy getCategoryAxisDriver();
	ValueAxisDriver getValueAxisDriver();
	
	List<BarChartLayer> getLayers();
	BarChartLayer getLayer(String id);
	BarChartLayer addLayer(BarChartLayer layer);
	BarChartLayer addSmoothLine(Series<CDecimal> series);
	BarChartLayer addPolyLine(Series<CDecimal> series);
	BarChartLayer addHistogram(Series<CDecimal> series);
	BarChart dropLayer(String id);
	BarChart dropLayer(BarChartLayer layer);
	
	List<ChartOverlay> getOverlays();
	BarChart addStaticOverlay(String text, int y);
	BarChart addOverlay(ChartOverlay overlay);
	
	ChartSpaceManager getHorizontalSpaceManager();
	ChartSpaceManager getVerticalSpaceManager();
	@Deprecated
	BarChart setZeroAtCenter(boolean zeroAtCenter);
	
}

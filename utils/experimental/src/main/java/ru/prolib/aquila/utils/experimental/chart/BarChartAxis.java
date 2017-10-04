package ru.prolib.aquila.utils.experimental.chart;

import ru.prolib.aquila.utils.experimental.chart.formatters.LabelFormatter;

public interface BarChartAxis {

	boolean isVisible();

	BarChartAxis setVisible(boolean visible);

	LabelFormatter<?> getLabelFormatter();

	BarChartAxis setLabelFormatter(LabelFormatter<?> labelFormatter);

	void paint(BarChartVisualizationContext context, AxisLabelProvider labelProvider);

}

package ru.prolib.aquila.utils.experimental.chart;

import ru.prolib.aquila.core.data.ObservableSeries;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDriver;

/**
 * Created by TiM on 08.09.2017.
 */
public interface BarChartPanel {
	
    BarChart addChart(String id);
    BarChart getChart(String id);

    void setPreferredNumberOfBars(int number);
    CategoryAxisDriver getCategoryAxisDriver();

    void paint();
    
    /**
     * Set series to use as set of categories.
     * <p>
     * Setting the categories is not mandatory and only required to properly worked scroll bar.
     * <p>
     * @param categories - any series to use as set of categories
     */
    void setCategories(ObservableSeries<?> categories);
    
    SelectedCategoryTracker getCategoryTracker();
}

package ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Created by TiM on 20.06.2017.
 */
public class RangeInfo {
    private final double minValue;
    private final double maxValue;
    private final double stepValue;
    private final double step;

    public RangeInfo(double minValue, double maxValue, double stepValue, double step) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.stepValue = stepValue;
        this.step = step;
    }

    public double getMinValue() {
        return minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public double getStepValue() {
        return stepValue;
    }

    public double getStep() {
        return step;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("minValue", minValue)
                .append("maxValue", maxValue)
                .append("stepValue", stepValue)
                .append("step", step)
                .toString();
    }
}

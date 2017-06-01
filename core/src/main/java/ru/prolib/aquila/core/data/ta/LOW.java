package ru.prolib.aquila.core.data.ta;

import ru.prolib.aquila.core.data.*;

/**
 * Объектно-ориентированная обертка функции {@link TAMath#qema(Series, int, int)}.
 */
public class LOW implements Series<Double> {
	private final String id;
	private final Series<Double> source;
	private final int period;
	private final TAMath math;

	public LOW(String id, Series<Candle> source, int period, TAMath math) {
		this.id = id;
		this.source = new CandleLowSeries(source);
		this.period = period;
		this.math = math;
	}

	public LOW(String id, Series<Candle> source, int period) {
		this(id, source, period, TAMath.getInstance());
	}

	public LOW(Series<Candle> source, int period) {
		this(DEFAULT_ID, source, period);
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Double get() throws ValueException {
		return math.min(source, getLength() - 2, period);
	}

	@Override
	public Double get(int index) throws ValueException {
		return math.min(source, index-1, period);
	}

	@Override
	public int getLength() {
		return source.getLength();
	}

}

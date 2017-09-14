package ru.prolib.aquila.core.data.tseries;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.data.TAMath;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.TimeFrame;
import ru.prolib.aquila.core.data.ValueException;

public class QEMATSeries implements TSeries<Double> {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(QEMATSeries.class);
	}
	
	private final String id;
	private final TSeries<Double> source;
	private final int period;
	private final TAMath math;
	
	public QEMATSeries(String id, TSeries<Double> source, int period, TAMath math) {
		this.id = id;
		this.source = source;
		this.period = period;
		this.math = math;
	}
	
	public QEMATSeries(String id, TSeries<Double> source, int period) {
		this(id, source, period, TAMath.getInstance());
	}
	
	public QEMATSeries(TSeries<Double> source, int period) {
		this(TSeries.DEFAULT_ID, source, period);
	}

	@Override
	public String getId() {
		return id;
	}
	
	public TSeries<Double> getSource() {
		return source;
	}
	
	public int getPeriod() {
		return period;
	}
	
	public TAMath getMath() {
		return math;
	}

	@Override
	public Double get() throws ValueException {
		lock();
		try {
			return math.qema(source, getLength() - 1, period);
		} finally {
			unlock();
		}
	}

	@Override
	public Double get(int index) throws ValueException {
		lock();
		try {
			return math.qema(source, index, period);
		} finally {
			unlock();
		}
	}

	@Override
	public int getLength() {
		return source.getLength();
	}

	@Override
	public LID getLID() {
		return source.getLID();
	}

	@Override
	public void lock() {
		source.lock();
	}

	@Override
	public void unlock() {
		source.unlock();
	}

	@Override
	public Double get(Instant time) {
		lock();
		try {
			int r = source.toIndex(time);
			return r < 0 ? null : math.qema(source, r, period);
		} catch ( ValueException e ) {
			logger.error("Unexpected exception: ", e);
			return null;
		} finally {
			unlock();
		}
	}

	@Override
	public TimeFrame getTimeFrame() {
		return source.getTimeFrame();
	}

	@Override
	public int toIndex(Instant time) {
		return source.toIndex(time);
	}

}
package ru.prolib.aquila.core.data.tseries;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.data.TAMath;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.core.data.ValueException;

public class MAXLongTSeries implements TSeries<Long> {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(MAXLongTSeries.class);
	}
	
	private final String id;
	private final TSeries<Long> source;
	private final int period;
	private final TAMath math;
	
	public MAXLongTSeries(String id, TSeries<Long> source, int period, TAMath math) {
		this.id = id;
		this.source = source;
		this.period = period;
		this.math = math;
	}
	
	public MAXLongTSeries(String id, TSeries<Long> source, int period) {
		this(id, source, period, TAMath.getInstance());
	}
	
	public MAXLongTSeries(TSeries<Long> source, int period) {
		this(TSeries.DEFAULT_ID, source, period);
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	public TSeries<Long> getSource() {
		return source;
	}
	
	public int getPeriod() {
		return period;
	}
	
	public TAMath getMath() {
		return math;
	}
	
	@Override
	public Long get() throws ValueException {
		lock();
		try {
			return math.maxL(source, period);
		} finally {
			unlock();
		}
	}
	
	@Override
	public Long get(int index) throws ValueException {
		lock();
		try {
			return math.maxL(source, index, period);
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
	public Long get(Instant time) {
		lock();
		try {
			int r = source.toIndex(time);
			return r < 0 ? null : math.maxL(source, r, period);
		} catch ( ValueException e ) {
			logger.error("Unexpected exception: ", e);
			return null;
		} finally {
			unlock();
		}
	}
	
	@Override
	public ZTFrame getTimeFrame() {
		return source.getTimeFrame();
	}
	
	@Override
	public int toIndex(Instant time) {
		return source.toIndex(time);
	}
	
	@Override
	public Instant toKey(int index) throws ValueException {
		return source.toKey(index);
	}

	@Override
	public int getFirstIndexBefore(Instant time) {
		return source.getFirstIndexBefore(time);
	}

	@Override
	public Long getFirstBefore(Instant time) {
		lock();
		try {
			int index = source.getFirstIndexBefore(time);
			return index >= 0 ? math.maxL(source, index, period) : null;
		} catch ( ValueException e ) {
			throw new IllegalStateException("Unexpected exception: ", e);
		} finally {
			unlock();
		}
	}

}

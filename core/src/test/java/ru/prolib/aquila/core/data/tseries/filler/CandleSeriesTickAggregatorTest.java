package ru.prolib.aquila.core.data.tseries.filler;

import static org.junit.Assert.*;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TSeriesImpl;
import ru.prolib.aquila.core.data.ZTFrame;

public class CandleSeriesTickAggregatorTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private TSeriesImpl<Candle> series;
	private CandleSeriesAggregator<Tick> aggregator;

	@Before
	public void setUp() throws Exception {
		series = new TSeriesImpl<>(ZTFrame.M5);
		aggregator = CandleSeriesTickAggregator.getInstance();
	}

	@Test
	public void testAggregate_FirstCandle() throws Exception {
		aggregator.aggregate(series, Tick.ofTrade(T("2017-05-02T11:36:53Z"),
				CDecimalBD.of("86.12"), CDecimalBD.of(1000L)));

		Interval interval = Interval.of(T("2017-05-02T11:35:00Z"), T("2017-05-02T11:40:00Z"));
		Candle expected = new Candle(interval, CDecimalBD.of("86.12"), CDecimalBD.of(1000L));
		assertEquals(1, series.getLength());
		assertEquals(expected, series.get());
	}
	
	@Test
	public void testAggregate_AppendToLastCandle() throws Exception {
		Interval interval = Interval.of(T("2017-05-02T11:50:00Z"), T("2017-05-02T11:55:00Z"));
		series.set(interval.getStart(), new Candle(interval, CDecimalBD.of("100.02"), CDecimalBD.of(500L)));
		
		aggregator.aggregate(series, Tick.ofTrade(T("2017-05-02T11:52:00Z"),
				CDecimalBD.of("98.13"), CDecimalBD.of(100L)));
		
		Candle expected = new Candle(interval,
				CDecimalBD.of("100.02"),
				CDecimalBD.of("100.02"),
				CDecimalBD.of("98.13"),
				CDecimalBD.of("98.13"),
				CDecimalBD.of(600L));
		assertEquals(1, series.getLength());
		assertEquals(expected, series.get());
	}
	
	@Test
	public void testAggregate_PastData() throws Exception {
		Interval interval1 = Interval.of(T("2017-05-02T11:50:00Z"), T("2017-05-02T11:55:00Z"));
		series.set(interval1.getStart(), new Candle(interval1, CDecimalBD.of("100.02"), CDecimalBD.of(500L)));
		
		aggregator.aggregate(series, Tick.ofTrade(T("2017-05-02T11:49:59Z"),
				CDecimalBD.of("98.13"), CDecimalBD.of(100L)));
		
		Interval interval2 = Interval.of(T("2017-05-02T11:45:00Z"), T("2017-05-02T11:50:00Z"));
		Candle expected1 = new Candle(interval2, CDecimalBD.of("98.13"), CDecimalBD.of(100L)),
				expected2 = new Candle(interval1, CDecimalBD.of("100.02"), CDecimalBD.of(500L));

		assertEquals(2, series.getLength());
		assertEquals(expected1, series.get(0));
		assertEquals(expected2, series.get(1));
	}
	
	@Test
	public void testAggregate_NewCandle() throws Exception {
		Interval interval1 = Interval.of(T("2017-05-02T11:50:00Z"), T("2017-05-02T11:55:00Z"));
		series.set(interval1.getStart(), new Candle(interval1, CDecimalBD.of("100.02"), CDecimalBD.of(500L)));

		aggregator.aggregate(series, Tick.ofTrade(T("2017-05-02T11:56:02Z"),
				CDecimalBD.of("98.13"), CDecimalBD.of(100L)));

		Interval interval2 = Interval.of(T("2017-05-02T11:55:00Z"), T("2017-05-02T12:00:00Z"));
		Candle expected2 = new Candle(interval2, CDecimalBD.of("98.13"), CDecimalBD.of(100L)),
				expected1 = new Candle(interval1, CDecimalBD.of("100.02"), CDecimalBD.of(500L));
		assertEquals(2, series.getLength());
		assertEquals(expected1, series.get(0));
		assertEquals(expected2, series.get(1));
	}
	
	@Test
	public void testEquals() {
		assertTrue(aggregator.equals(CandleSeriesTickAggregator.getInstance()));
		assertFalse(aggregator.equals(null));
		assertFalse(aggregator.equals(this));
	}

}

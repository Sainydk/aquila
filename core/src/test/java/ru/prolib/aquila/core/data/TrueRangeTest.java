package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;
import org.joda.time.*;
import org.junit.*;

/**
 * 2013-03-12<br>
 * $Id: TRTest.java 571 2013-03-12 00:53:34Z whirlwind $
 */
public class TrueRangeTest {
	private static final Double fixture[][] = {
		// hi, lo, close, tr expected
		{ 48.70d, 47.79d, 48.16d, 0.91d },
		{ 49.35d, 48.86d, 49.32d, 1.19d },// HL=0.49 HCp=1.19 LCp=0.70: TR=1.19
		{ 49.92d, 49.50d, 49.91d, 0.60d },
		{ 50.19d, 49.87d, 50.13d, 0.32d },// HL=0.32 HCp=0.28 LCp=0.04: TR=0.32
		{ 50.12d, 49.20d, 49.53d, 0.93d },
	};
	private EditableCandleSeries source;
	private TrueRange indicator;

	@Before
	public void setUp() throws Exception {
		source = new CandleSeriesImpl(TimeFrame.M1);
		indicator = new TrueRange("foo", source);
	}
	
	@Test
	public void testConstruct2() throws Exception {
		assertEquals("foo", indicator.getId());
	}
	
	@Test
	public void testConstruct1() throws Exception {
		indicator = new TrueRange(source);
		assertEquals(Series.DEFAULT_ID, indicator.getId());
	}
	
	@Test
	public void testGet() throws Exception {
		DateTime time = new DateTime(2013, 10, 11, 11, 12, 34);
		TimeFrame tf = source.getTimeFrame();
		for ( int i = 0; i < fixture.length; i ++ ) {
			source.add(new Candle(tf.getInterval(time.plusMinutes(i)), 0d,
					fixture[i][0], fixture[i][1], fixture[i][2], 0));
			String msg = "At #" + i;
			assertEquals(msg, fixture[i][3], indicator.get(), 0.01d);
			assertEquals(msg, fixture[i][3], indicator.get(i),0.01d);
		}
		assertEquals(fixture.length, indicator.getLength());
	}

}
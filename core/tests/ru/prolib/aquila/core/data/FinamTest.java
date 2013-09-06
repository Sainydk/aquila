package ru.prolib.aquila.core.data;


import static org.junit.Assert.*;

import java.io.File;
import java.text.SimpleDateFormat;

import org.junit.*;

/**
 * 2013-03-08<br>
 * $Id: FinamTest.java 565 2013-03-10 19:32:12Z whirlwind $
 */
public class FinamTest {
	private static SimpleDateFormat df;
	private static Finam finam;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		df = new SimpleDateFormat("yyyyMMddHHmmss");
		finam = new Finam();
	}

	@Before
	public void setUp() throws Exception {
		
	}
	
	@Test
	public void testLoadCandles_WithVolume() throws Exception {
		Object fix[][] = {
			{ "20130307100000", 134.97d, 135.21d, 134.52d, 134.67d, 5386540L },
			{ "20130307110000", 134.68d, 135.59d, 134.62d, 135.40d, 1906010L },
		};
		EditableSeries<Candle> expected = new SeriesImpl<Candle>();
		for ( int i = 0; i < fix.length; i ++ ) {
			expected.add(new Candle(df.parse((String) fix[i][0]),
					(Double) fix[i][1], (Double) fix[i][2],
					(Double) fix[i][3], (Double) fix[i][4],
					(Long) fix[i][5]));
		}
		
		EditableSeries<Candle> actual = new SeriesImpl<Candle>();
		finam.loadCandles(new File("fixture/GAZP.txt"), actual);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testLoadCandles_WithoutVolume() throws Exception {
		Object fix[][] = {
				{ "20130301100000",152910.0,152910.0,152270.0,152350.0 },
				{ "20130301100500",152350.0,152430.0,152260.0,152370.0 },
				{ "20130301101000",152370.0,152380.0,152060.0,152200.0 },
				{ "20130301101500",152200.0,152250.0,152130.0,152190.0 },
		};
		EditableSeries<Candle> expected = new SeriesImpl<Candle>();
		for ( int i = 0; i < fix.length; i ++ ) {
			expected.add(new Candle(df.parse((String) fix[i][0]),
					(Double) fix[i][1], (Double) fix[i][2],
					(Double) fix[i][3], (Double) fix[i][4], 0L));
		}

		EditableSeries<Candle> actual = new SeriesImpl<Candle>();
		finam.loadCandles(new File("fixture/SPFB.RTS.txt"), actual);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCreateWriter() throws Exception {
		File file = File.createTempFile("finam-", ".csv");
		EditableCandleSeries candles = new CandleSeriesImpl();
		
		CandlesWriter expected = new FinamCandlesCsvFileWriter(candles, file);
		assertEquals(expected, finam.createWriter(file, candles));
	}

}

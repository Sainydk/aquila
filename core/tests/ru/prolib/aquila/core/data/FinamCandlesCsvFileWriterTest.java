package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.BasicConfigurator;
import org.junit.*;

public class FinamCandlesCsvFileWriterTest {
	private FinamCandlesCsvFileWriter writer;
	private EditableCandleSeries candles;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		candles = new SeriesFactoryImpl().createCandle();
	}
	
	/**
	 * Создать копию файла фикстуры.
	 * <p>
	 * @param fixture имя файла фикстуры
	 * @return файл-копия
	 * @throws Exception
	 */
	private File makeCopy(String fixture) throws Exception {
		File dst = File.createTempFile("finam-", ".csv");
		FileUtils.copyFile(new File(fixture), dst);
		return dst;
	}
	
	/**
	 * Ярлык для создания свечи.
	 * @param time строка времени в формате yyyyMMdd HHmmss
	 * @param open
	 * @param high
	 * @param low
	 * @param close
	 * @param volume
	 * @return
	 * @throws ParseException
	 */
	private Candle candle(String time, double open, double high, double low,
			double close, long volume) throws ParseException
	{
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd HHmmss");
		return new Candle(df.parse(time), open, high, low, close, volume);
	}
	
	@Test
	public void testWrite_WithHeaders() throws Exception {
		File dst = File.createTempFile("finam-", ".csv");
		dst.deleteOnExit();
		
		writer = new FinamCandlesCsvFileWriter(candles, dst);
		writer.start();
		
		candles.add(candle("20130906 150718", 18.1d, 19.8d, 17.5d, 17.5d, 124));
		candles.add(candle("20130906 150724", 12.4d, 23.3d, 11.1d, 15.0d, 832));
		candles.add(candle("20130906 150815", 11.4d, 18.2d, 11.2d, 18.3d, 452));
		
		writer.stop();
		
		EditableCandleSeries loaded = new SeriesFactoryImpl().createCandle();
		new Finam().loadCandles(dst, loaded);
		
		assertEquals(candles, loaded);
	}
	
	@Test
	public void testWrite_WithoutHeaders() throws Exception {
		File dst = makeCopy("fixture/GAZP.txt");
		dst.deleteOnExit();
		
		new Finam().loadCandles(dst, candles);
		writer = new FinamCandlesCsvFileWriter(candles, dst);
		writer.start();
		
		candles.add(candle("20130906 150718", 18.1d, 19.8d, 17.5d, 17.5d, 124));
		candles.add(candle("20130906 150724", 12.4d, 23.3d, 11.1d, 15.0d, 832));
		candles.add(candle("20130906 150815", 11.4d, 18.2d, 11.2d, 18.3d, 452));
		
		writer.stop();
		
		EditableCandleSeries loaded = new SeriesFactoryImpl().createCandle();
		new Finam().loadCandles(dst, loaded);
		assertEquals(candles, loaded);
		assertEquals(5, candles.getLength());
		assertEquals(candles.getLength(), loaded.getLength());
	}
	
	@Test
	public void testEquals() throws Exception {
		File dst = File.createTempFile("finam-", ".csv");
		dst.deleteOnExit();
		writer = new FinamCandlesCsvFileWriter(candles, dst);
		assertTrue(writer.equals(writer));
		assertFalse(writer.equals(null));
		assertFalse(writer.equals(this));
		assertTrue(writer.equals(new FinamCandlesCsvFileWriter(candles, dst)));
	}

}
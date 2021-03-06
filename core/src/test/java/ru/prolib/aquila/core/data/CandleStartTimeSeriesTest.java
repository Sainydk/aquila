package ru.prolib.aquila.core.data;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.concurrency.LID;

import java.time.Instant;

import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;

/**
 * Created by TiM on 04.05.2017.
 */
public class CandleStartTimeSeriesTest {
	private IMocksControl control;
	private Series<Candle> candlesMock;
    private SeriesImpl<Candle> candles;
    private Instant time1, time2, time3;
    private Candle candle1, candle2, candle3;
    private CandleStartTimeSeries series;

    @SuppressWarnings("unchecked")
	@Before
    public void setUp() throws Exception {
		control = createStrictControl();
		candlesMock = control.createMock(Series.class);
        candles = new SeriesImpl<Candle>("CANDLES");
        time1 = Instant.parse("2017-05-04T11:00:00Z");
        time2 = time1.plusSeconds(5 * 60);
        time3 = time2.plusSeconds(5 * 60);
		candle1 = new CandleBuilder()
				.withTime(time1)
				.withTimeFrame(ZTFrame.M5)
				.withOpenPrice(CDecimalBD.of("144440"))
				.withHighPrice(CDecimalBD.of("144440"))
				.withLowPrice(CDecimalBD.of("143130"))
				.withClosePrice(CDecimalBD.of("143210"))
				.withVolume(CDecimalBD.of(39621L))
				.buildCandle();
		candle2 = new CandleBuilder()
				.withTime(time2)
				.withTimeFrame(ZTFrame.M5)
				.withOpenPrice(CDecimalBD.of("143230"))
				.withHighPrice(CDecimalBD.of("143390"))
				.withLowPrice(CDecimalBD.of("143100"))
				.withClosePrice(CDecimalBD.of("143290"))
				.withVolume(CDecimalBD.of(12279L))
				.buildCandle();
		candle3 = new CandleBuilder()
				.withTime(time3)
				.withTimeFrame(ZTFrame.M5)
				.withOpenPrice(CDecimalBD.of("143280"))
				.withHighPrice(CDecimalBD.of("143320"))
				.withLowPrice(CDecimalBD.of("143110"))
				.withClosePrice(CDecimalBD.of("143190"))
				.withVolume(CDecimalBD.of(11990L))
				.buildCandle();
        candles.add(candle1);
        candles.add(candle2);
        candles.add(candle3);
        series = new CandleStartTimeSeries(candles);
    }

    @Test
    public void testGetId() throws Exception {
        assertEquals("CANDLES.START_TIME", series.getId());
    }

    @Test
    public void testGet1() throws Exception {
        assertEquals(time3, series.get());
    }

    @Test
    public void testGet2() throws Exception {
        assertEquals(time1, series.get(0));
        assertEquals(time2, series.get(1));
        assertEquals(time3, series.get(2));
    }

    @Test
    public void testGetLength() throws Exception {
        assertEquals(3, series.getLength());
    }

	@Test
	public void testGetLID() {
		LID lidStub = LID.createInstance();
		expect(candlesMock.getLID()).andReturn(lidStub);
		control.replay();
		
		series = new CandleStartTimeSeries(candlesMock);
		assertSame(lidStub, series.getLID());
		
		control.verify();
	}
	
	@Test
	public void testLock() {
		candlesMock.lock();
		control.replay();
		
		series = new CandleStartTimeSeries(candlesMock);
		series.lock();
		
		control.verify();
	}
	
	@Test
	public void testUnlock() {
		candlesMock.unlock();
		control.replay();
		
		series = new CandleStartTimeSeries(candlesMock);
		series.unlock();
		
		control.verify();
	}	

}
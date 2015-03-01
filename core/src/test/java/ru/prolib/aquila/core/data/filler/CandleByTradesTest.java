package ru.prolib.aquila.core.data.filler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.utils.Variant;

public class CandleByTradesTest {
	private IMocksControl control;
	private EditableCandleSeries candles;
	private Security security;
	private EventTypeSI type;
	private Trade trade;
	private CandleByTrades updater;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		candles = control.createMock(EditableCandleSeries.class);
		security = control.createMock(Security.class);
		type = control.createMock(EventTypeSI.class);
		trade = control.createMock(Trade.class);
		updater = new CandleByTrades(security, candles);
		
		expect(security.OnTrade()).andStubReturn(type);
	}
	
	@Test
	public void testStart() throws Exception {
		type.addListener(same(updater));
		control.replay();
		
		updater.start();
		
		control.verify();
	}
	
	@Test
	public void testStop() throws Exception {
		type.removeListener(same(updater));
		control.replay();
		
		updater.stop();
		
		control.verify();
	}
	
	@Test
	public void testOnEvent() throws Exception {
		candles.aggregate(same(trade), eq(true));
		control.replay();
		
		updater.onEvent(new SecurityTradeEvent(type, security, trade));
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(updater.equals(updater));
		assertFalse(updater.equals(this));
		assertFalse(updater.equals(null));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<Security> vSec = new Variant<Security>()
			.add(security)
			.add(control.createMock(Security.class));
		Variant<EditableCandleSeries> vAggr =
				new Variant<EditableCandleSeries>(vSec)
			.add(candles)
			.add(control.createMock(EditableCandleSeries.class));
		Variant<?> iterator = vAggr;
		int foundCnt = 0;
		CandleByTrades x, found = null;
		do {
			x = new CandleByTrades(vSec.get(), vAggr.get());
			if ( updater.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(security, found.getSecurity());
		assertSame(candles, found.getCandles());
	}

}

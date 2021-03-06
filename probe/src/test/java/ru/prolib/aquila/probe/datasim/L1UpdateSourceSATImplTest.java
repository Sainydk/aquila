package ru.prolib.aquila.probe.datasim;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.L1UpdateConsumer;
import ru.prolib.aquila.core.BusinessEntities.SecurityEvent;
import ru.prolib.aquila.core.BusinessEntities.SecurityField;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.DataProviderStub;
import ru.prolib.aquila.data.L1UpdateSource;

public class L1UpdateSourceSATImplTest {
	private static Symbol symbol1, symbol2, symbol3;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		symbol1 = new Symbol("AAPL");
		symbol2 = new Symbol("MSFT");
		symbol3 = new Symbol("SBER");
	}
	
	private IMocksControl control;
	private L1UpdateSource basicSourceMock;
	private Set<EditableSecurity> pending;
	private L1UpdateSourceSATImpl source;
	private EditableTerminal terminal;
	private EditableSecurity security1, security2, security3;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		basicSourceMock = control.createMock(L1UpdateSource.class);
		pending = new HashSet<>();
		source = new L1UpdateSourceSATImpl(basicSourceMock, pending);
		terminal = new BasicTerminalBuilder()
			.withDataProvider(new DataProviderStub())
			.buildTerminal();
		security1 = terminal.getEditableSecurity(symbol1);
		security2 = terminal.getEditableSecurity(symbol2);
		security3 = terminal.getEditableSecurity(symbol3);
	}
	
	@Test
	public void testClose() throws Exception {
		pending.add(security1);
		pending.add(security2);
		pending.add(security3);
		control.replay();
		
		source.close();
		
		control.verify();
		assertEquals(new HashSet<>(), pending);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSubscribeL1_ThrowsIfNotASecurity() throws Exception {
		L1UpdateConsumer consumerMock = control.createMock(L1UpdateConsumer.class);
		control.replay();
		
		source.subscribeL1(symbol1, consumerMock);
	}
	
	@Test
	public void testSubscribeL1_IfSecurityNotAvailable() throws Exception {
		control.replay();
		
		source.subscribeL1(symbol2, security2);
		
		control.verify();
		assertTrue(pending.contains(security2));
		assertTrue(security2.onAvailable().isListener(source));
	}
	
	@Test
	public void testSubscribeL1_IfSecurityAvailable() throws Exception {
		security3.consume(new DeltaUpdateBuilder()
			.withToken(SecurityField.DISPLAY_NAME, "foo")
			.withToken(SecurityField.LOT_SIZE, CDecimalBD.of(1L))
			.withToken(SecurityField.TICK_SIZE, CDecimalBD.of("0.01"))
			.withToken(SecurityField.TICK_VALUE, CDecimalBD.ofRUB2("0.01"))
			.withToken(SecurityField.SETTLEMENT_PRICE, CDecimalBD.ofRUB2("100"))
			.buildUpdate());
		basicSourceMock.subscribeL1(symbol3, security3);
		control.replay();
		
		source.subscribeL1(symbol3, security3);
		
		control.verify();
		assertEquals(new HashSet<>(), pending);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testUnsubscribeL1_ThrowsIfNotASecurity() throws Exception {
		L1UpdateConsumer consumerMock = control.createMock(L1UpdateConsumer.class);
		control.replay();
		
		source.unsubscribeL1(symbol1, consumerMock);
	}
	
	@Test
	public void testUnsubscribeL1_IfPendingSubscription() throws Exception {
		pending.add(security2);
		control.replay();
		
		source.unsubscribeL1(symbol2, security2);
		
		control.verify();
		assertEquals(new HashSet<>(), pending);
	}
	
	@Test
	public void testUnsubscribeL1_IfNotPendingSubscription() throws Exception {
		basicSourceMock.unsubscribeL1(symbol1, security1);
		control.replay();
		
		source.unsubscribeL1(symbol1, security1);
		
		control.verify();
		assertEquals(new HashSet<>(), pending);
	}
	
	@Test
	public void testOnEvent_() throws Exception {
		Instant t = Instant.parse("2017-08-06T19:50:00Z");
		pending.add(security2);
		security2.onAvailable().addListener(source);
		basicSourceMock.setStartTimeL1(symbol2, t);
		basicSourceMock.subscribeL1(symbol2, security2);
		control.replay();
		
		source.onEvent(new SecurityEvent(security2.onAvailable(), security2, t));
		
		control.verify();
		assertFalse(pending.contains(security2));
		assertFalse(security2.onAvailable().isListener(source));
	}
	
	@Test (expected=UnsupportedOperationException.class)
	public void testSetStartTimeL1() throws Exception {
		source.setStartTimeL1(symbol1, Instant.EPOCH);
	}

}

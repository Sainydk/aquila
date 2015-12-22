package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

public class TerminalEventDispatcherTest {
	private static Symbol symbol;
	private IMocksControl control;
	private EventSystem es;
	private EventListener listener;
	private EventQueue queue;
	private Terminal terminal;
	private TerminalEventDispatcher dispatcher;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		symbol = new Symbol("RI", "SPBFUT", "USD", SymbolType.FUTURE);
	}
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		queue = control.createMock(EventQueue.class);
		listener = control.createMock(EventListener.class);
		terminal = control.createMock(Terminal.class);
		es = new EventSystemImpl(queue);
		dispatcher = new TerminalEventDispatcher(es);
	}
	
	@Test
	public void testStructure() throws Exception {
		EventDispatcher ed = dispatcher.getEventDispatcher();
		assertEquals("Terminal", ed.getId());
		
		EventType type;
		type = dispatcher.OnConnected();
		assertEquals("Terminal.Connected", type.getId());
		assertFalse(type.isOnlySyncMode());
		
		type = dispatcher.OnDisconnected();
		assertEquals("Terminal.Disconnected", type.getId());
		assertFalse(type.isOnlySyncMode());
		
		type = dispatcher.OnPanic();
		assertEquals("Terminal.Panic", type.getId());
		assertFalse(type.isOnlySyncMode());
		
		type = dispatcher.OnRequestSecurityError();
		assertEquals("Terminal.RequestSecurityError", type.getId());
		assertFalse(type.isOnlySyncMode());
		
		type = dispatcher.OnStarted();
		assertEquals("Terminal.Started", type.getId());
		assertFalse(type.isOnlySyncMode());

		type = dispatcher.OnStopped();
		assertEquals("Terminal.Stopped", type.getId());
		assertFalse(type.isOnlySyncMode());
		
		type = dispatcher.OnReady();
		assertEquals("Terminal.Ready", type.getId());
		assertTrue(type.isOnlySyncMode());
		
		type = dispatcher.OnUnready();
		assertEquals("Terminal.Unready", type.getId());
		assertTrue(type.isOnlySyncMode());
	}
	
	@Test
	public void testFireConnected() throws Exception {
		dispatcher.OnConnected().addListener(listener);
		queue.enqueue(eq(new EventImpl(dispatcher.OnConnected())));
		control.replay();
		
		dispatcher.fireConnected(terminal);
		
		control.verify();
	}
	
	@Test
	public void testFireDisconnected() throws Exception {
		dispatcher.OnDisconnected().addListener(listener);
		queue.enqueue(eq(new EventImpl(dispatcher.OnDisconnected())));
		control.replay();
			
		dispatcher.fireDisconnected(terminal);
		
		control.verify();
	}
	
	@Test
	public void testFireStarted() throws Exception {
		dispatcher.OnStarted().addListener(listener);
		queue.enqueue(eq(new EventImpl(dispatcher.OnStarted())));
		control.replay();
		
		dispatcher.fireStarted();
		
		control.verify();
	}
	
	@Test
	public void testFireStopped() throws Exception {
		dispatcher.OnStopped().addListener(listener);
		queue.enqueue(eq(new EventImpl(dispatcher.OnStopped())));
		control.replay();
		
		dispatcher.fireStopped();
		
		control.verify();
	}
	
	@Test
	public void testFirePanic2() throws Exception {
		dispatcher.OnPanic().addListener(listener);
		queue.enqueue(eq(new PanicEvent(dispatcher.OnPanic(), 80, "test")));
		control.replay();
		
		dispatcher.firePanic(80, "test");
		
		control.verify();
	}

	@Test
	public void testFirePanic3() throws Exception {
		Object args[] = { "foo", 23 };
		dispatcher.OnPanic().addListener(listener);
		queue.enqueue(eq(new PanicEvent(dispatcher.OnPanic(), 13, "foo", args)));
		control.replay();
		
		dispatcher.firePanic(13, "foo", args);
		
		control.verify();
	}

	@Test
	public void testRequestSecurityError() throws Exception {
		EventType type = dispatcher.OnRequestSecurityError(); 
		type.addListener(listener);
		queue.enqueue(eq(new RequestSecurityEvent(type, symbol, 500, "test")));
		control.replay();
		
		dispatcher.fireSecurityRequestError(symbol, 500, "test");
		
		control.verify();
	}
	
	@Test
	public void testFireReady() throws Exception {
		dispatcher.OnReady().addListener(listener);
		queue.enqueue(eq(new EventImpl(dispatcher.OnReady())));
		control.replay();
		
		dispatcher.fireReady();
		
		control.verify();
	}
	
	@Test
	public void testFireUnready() throws Exception {
		dispatcher.OnUnready().addListener(listener);
		queue.enqueue(eq(new EventImpl(dispatcher.OnUnready())));
		control.replay();
		
		dispatcher.fireUnready();
		
		control.verify();
	}

}

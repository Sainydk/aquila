package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.junit.Assert.*;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

public class SecuritiesEventDispatcherTest {
	private static SecurityDescriptor descr =
			new SecurityDescriptor("zu", "lu", ISO4217.USD, SecurityType.FUT);
	@SuppressWarnings("rawtypes")
	private EditableTerminal terminal;
	private EditableSecurity security;
	private SecuritiesEventDispatcher dispatcher;
	private List<Event> eventsActual, eventsExpected;
	private Event e;

	@SuppressWarnings("rawtypes")
	@Before
	public void setUp() throws Exception {
		terminal = new TerminalImpl("test");
		security = terminal.getEditableSecurity(descr);
		dispatcher = new SecuritiesEventDispatcher(terminal.getEventSystem());
		terminal.getEventSystem().getEventQueue().start();
		eventsActual = new Vector<Event>();
		eventsExpected = new Vector<Event>();
		e = null;
	}
	
	@After
	public void tearDown() throws Exception {
		terminal.getEventSystem().getEventQueue().stop();
		terminal.getEventSystem().getEventQueue().join(5000L);
	}

	/**
	 * Тестировать синхронное событие.
	 * <p>
	 * @param eventType тип события, которое должно быть синхронным
	 * @param expected ожидаемое итоговое событие (результат ретрансляции)
	 * @param incoming исходное событие (основание ретрансляции)
	 * @throws Exception
	 */
	private final void testSynchronousEvent(EventType eventType,
			Event expected, Event incoming) throws Exception
	{
		final CountDownLatch counter1 = new CountDownLatch(1),
				counter2 = new CountDownLatch(1);
		// Используем этот тип события для симуляции асинхронного события 
		dispatcher.OnAvailable().addListener(new EventListener() {
			@Override public void onEvent(Event event) {
				try {
					// Приостановить обработку очереди минимум на пол секунды
					assertTrue(counter1.await(500L, TimeUnit.MILLISECONDS));
					eventsActual.add(event);
					counter2.countDown();
				} catch ( InterruptedException e ) { }
			}
		});
		// Навешиваем обозревателя на тестируемый синхронный тип событий
		eventType.addListener(new EventListener() {
			@Override public void onEvent(Event event) {
				eventsActual.add(event);
				counter1.countDown(); // разблокировать асинхронную очередь
			}
		});
		// Ожидаемое событие (например, об изменении позиции) будет
		// ретранслировано непосредственно в момент поступления исходного
		// события. Для диспетчеризации этого события будет использована
		// отдельная очередь событий.
		eventsExpected.add(expected);
		// Асинхронное событие окажется на втором месте, так как очередь будет
		// на время заморожена обработчиком этого события.
		e = new SecurityEvent(dispatcher.OnAvailable(), security);
		eventsExpected.add(e);
		
		dispatcher.fireAvailable(security);
		dispatcher.onEvent(incoming);
		assertTrue(counter2.await(500L, TimeUnit.MILLISECONDS));

		assertEquals(eventsExpected, eventsActual);
	}

	@Test
	public void testFireAvailable() throws Exception {
		e = new SecurityEvent(dispatcher.OnAvailable(), security);
		eventsExpected.add(e);
		final CountDownLatch counter = new CountDownLatch(1);
		dispatcher.OnAvailable().addListener(new EventListener() {
			@Override public void onEvent(Event event) {
				eventsActual.add(event);
				counter.countDown();
			}
		});

		dispatcher.fireAvailable(security);
		assertTrue(counter.await(500L, TimeUnit.MILLISECONDS));
		assertEquals(eventsExpected, eventsActual);
	}
	
	@Test
	public void testOnEvent_OnChanged() throws Exception {
		testSynchronousEvent(dispatcher.OnChanged(),
				new SecurityEvent(dispatcher.OnChanged(), security),
				new SecurityEvent(security.OnChanged(), security));
	}
	
	@Test
	public void testOnEvent_OnTrade() throws Exception {
		Trade t = new Trade(terminal);
		testSynchronousEvent(dispatcher.OnTrade(),
				new SecurityTradeEvent(dispatcher.OnTrade(), security, t),
				new SecurityTradeEvent(security.OnTrade(), security, t));
	}
	
	@Test
	public void testStartRelayFor() throws Exception {
		dispatcher.startRelayFor(security);
		
		assertTrue(security.OnChanged().isListener(dispatcher));
		assertTrue(security.OnTrade().isListener(dispatcher));
	}
	
	@Test
	public void testStopRelayFor() throws Exception {
		security.OnChanged().addListener(dispatcher);
		security.OnTrade().addListener(dispatcher);
		
		dispatcher.stopRelayFor(security);
		
		assertFalse(security.OnChanged().isListener(dispatcher));
		assertFalse(security.OnTrade().isListener(dispatcher));
	}

}
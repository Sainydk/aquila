package ru.prolib.aquila.core;

import static org.junit.Assert.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.*;

/**
 * 2012-04-20<br>
 * $Id: EventQueueImplTest.java 513 2013-02-11 01:17:18Z whirlwind $
 */
public class EventQueueImplTest {
	private static EventSystem eSys;
	private static EventQueueImpl queue;
	private static EventDispatcher dispatcher;
	private static EventTypeSI type1,type2;

	@BeforeClass
	public static void setUpBeforeClass() {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.DEBUG);
	}

	@Before
	public void setUp() throws Exception {
		queue = new EventQueueImpl("EVNT");
		eSys = new EventSystemImpl(queue);
		dispatcher = eSys.createEventDispatcher();
		type1 = dispatcher.createType();
		type2 = dispatcher.createType();
		
	}
	
	@After
	public void tearDown() throws Exception {
		queue.stop();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testStart_ThrowsIfAlreadyStarted() throws Exception {
		queue.start();
		queue.start();
	}

	@Test
	public void testStarted() throws Exception {
		assertFalse(queue.started());
		queue.start();
		assertTrue(queue.started());
		queue.stop();
		assertTrue(queue.join(1000));
		assertFalse(queue.started());
	}
	
	@Test
	public void testStartStop_SequentiallyOk() throws Exception {
		queue.start();
		queue.stop();
		assertTrue(queue.join(1000));
		queue.stop();
		queue.start();
		queue.stop();
		assertTrue(queue.join(1000));
	}
	
	@Test (expected=IllegalStateException.class)
	public void testEnqueue_ThrowsIfNotStarted() throws Exception {
		queue.enqueue(new EventImpl(type1));
	}
	
	@Test (expected=NullPointerException.class)
	public void testEnqueue_ThrowsIfEventIsNull() throws Exception {
		queue.start();
		queue.enqueue(null);
	}
	
	@Test
	public void testEnqueue_AddListenerAfterEnqueue() throws Exception {
		// Добавление наблюдателя после помещения события в очередь
		// учитывается при непосредственно отправке события.
		final CountDownLatch finished = new CountDownLatch(3);
		EventDispatcher dispatcher = eSys.createEventDispatcher();
		type1 = dispatcher.createType();
		type2 = dispatcher.createType();
		type1.addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				finished.countDown();
			}
		});
		type2.addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				if ( event.isType(type2) ) {
					type1.addListener(this);
				}
				finished.countDown();
			}
		});
		queue.start();
		queue.enqueue(new EventImpl(type2));
		queue.enqueue(new EventImpl(type1));
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
		queue.stop();
	}
	
	private static int counter = 0; 
	@Test
	public void testEnqueueL_FromQueueThread() throws Exception {
		// Тест трансляции события из потока диспетчеризации.
		type1.addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				counter ++;
				if ( counter <= 10 ) {
					queue.enqueue(new EventImpl(type1));
				} else {
					queue.stop();
				}
			}
		});
		counter = 0;
		queue.start();
		queue.enqueue(new EventImpl(type1));
		assertTrue(queue.join(1000));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testJoin1_ThrowsIfTimeoutLessOrEqThanZero() throws Exception {
		queue.join(0);
	}
	
	@Test
	public void testJoin1_TrueIfFinished() throws Exception {
		final EventSI event = new EventImpl(type1);
		type1.addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				try {
					Thread.sleep(40);
					queue.stop();
				} catch ( Exception e ) {
					fail("Unhandled exception: " + e);
					Thread.currentThread().interrupt();
				}
			}
		});
		queue.start();
		queue.enqueue(event);
		assertTrue(queue.join(100));
		assertFalse(queue.started());
	}
	
	@Test
	public void testJoin1_FalseIfNotFinished() throws Exception {
		queue.start();
		long start = System.currentTimeMillis();
		assertFalse(queue.join(50));
		assertTrue(System.currentTimeMillis() - start >= 50);
		assertTrue(queue.started());
		queue.stop();
	}
	
	@Test
	public void testJoin1_IgnoreInQueueThread() throws Exception {
		final CountDownLatch exit = new CountDownLatch(1);
		final EventSI event = new EventImpl(type1);
		type1.addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				try {
					queue.join(1000);
				} catch ( InterruptedException e ) {
					fail("Unhandled exception: " + e);
					Thread.currentThread().interrupt();
				}
				exit.countDown();
			}
		});
		queue.start();
		queue.enqueue(event);
		assertTrue(exit.await(100, TimeUnit.MILLISECONDS));
		queue.stop();
	}
	
	@Test
	public void testJoin1_IgnoreIfQueueStopped() throws Exception {
		long start = System.currentTimeMillis();
		assertTrue(queue.join(1000));
		assertTrue(System.currentTimeMillis() - start <= 10);
	}
	
	@Test
	public void testJoin0_ReturnIfFinished() throws Exception {
		final CountDownLatch finished = new CountDownLatch(1);
		type1.addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				try {
					queue.stop();
				} catch ( Exception e ) {
					fail("Unhandled exception: " + e);
				}
				finished.countDown();
			}
		});
		queue.start();
		queue.enqueue(new EventImpl(type1));
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
		queue.join();
		assertFalse(queue.started());
	}
	
	@Test
	public void testJoin0_IgnoreInQueueThread() throws Exception {
		final CountDownLatch finished = new CountDownLatch(1);
		type1.addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				try {
					queue.join();
				} catch ( InterruptedException e ) {
					Thread.currentThread().interrupt();
					fail("Unhandled exception: " + e);
				}
				finished.countDown();
			}
		});
		queue.start();
		queue.enqueue(new EventImpl(type1));
		assertTrue(finished.await(50, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testJoin0_ReturnIfQueueStopped() throws Exception {
		long start = System.currentTimeMillis();
		queue.join();
		assertTrue(System.currentTimeMillis() - start <= 10);
	}
	
	@Test
	public void testJoin0_Ok() throws Exception {
		final CountDownLatch started = new CountDownLatch(1);
		type1.addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				try {
					started.await();
					Thread.sleep(50);
					queue.stop();
				} catch ( Exception e ) {
					Thread.currentThread().interrupt();
					fail("Unhandled exception: " + e);
				}
			}
		});
		queue.start();
		queue.enqueue(new EventImpl(type1));
		started.countDown();
		queue.join();
		assertFalse(queue.started());
	}
	
	@Test
	public void testFunctionalTest() throws Exception {
		new EventQueue_FunctionalTest().testSchedulingSequence(queue);
	}
	
}

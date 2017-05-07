package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventListenerStub;
import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventQueueImpl;

public class ObservableSeriesImplTest {
	private EventQueue queue;
	private EventListenerStub listenerStub;
	private SeriesImpl<Double> source;
	private ObservableSeriesImpl<Double> series; 

	@Before
	public void setUp() throws Exception {
		queue = new EventQueueImpl();
		listenerStub = new EventListenerStub();
		source = new SeriesImpl<>("foo");
		series = new ObservableSeriesImpl<>(queue, source);
	}
	
	@After
	public void tearDown() throws Exception {
		queue.stop();
	}
	
	@Test
	public void testCtor() {
		assertEquals("foo", series.getId());
		assertEquals("foo.SET", series.onSet().getId());
		assertEquals("foo.ADD", series.onAdd().getId());
	}
	
	@Test
	public void testGet0() throws Exception {
		source.add(25.04d);
		source.add(17.02d);
		
		assertEquals(17.02d, series.get(), 0.001d);
	}
	
	@Test
	public void testGet1() throws Exception {
		source.add(25.04d);
		source.add(17.02d);
		source.add(95.12d);
		
		assertEquals(25.04d, series.get(0), 0.001d);
		assertEquals(17.02d, series.get(1), 0.001d);
		assertEquals(95.12d, series.get(2), 0.001d);

		assertEquals(17.02d, series.get(-1), 0.001d);
		assertEquals(25.04d, series.get(-2), 0.001d);
	}
	
	@Test
	public void testGetLength() throws Exception {
		assertEquals(0, series.getLength());
		source.add(25.04d);
		assertEquals(1, series.getLength());
		source.add(17.02d);
		assertEquals(2, series.getLength());
		source.add(95.12d);
		assertEquals(3, series.getLength());
	}
	
	@Test
	public void testSet() throws Exception {
		source.add(25.04d);
		source.add(17.02d);
		source.add(95.12d);
		series.onAdd().addSyncListener(listenerStub);
		series.onSet().addSyncListener(listenerStub);
		
		series.set(86.19d);
		
		assertEquals(86.19d, series.get(), 0.001d);
		assertEquals(1, listenerStub.getEventCount());
		assertEquals(new SeriesEvent<Double>(series.onSet(), 2, 86.19d), listenerStub.getEvent(0));
	}
	
	@Test
	public void testAdd() throws Exception {
		source.add(25.04d);
		series.onAdd().addSyncListener(listenerStub);
		series.onSet().addSyncListener(listenerStub);

		series.add(42.14d);
		
		assertEquals(42.14d, series.get(), 0.001d);
		assertEquals(1, listenerStub.getEventCount());
		assertEquals(new SeriesEvent<Double>(series.onAdd(), 1, 42.14d), listenerStub.getEvent(0));
	}
	
	@Test
	public void testClear() throws Exception {
		source.add(25.04d);
		source.add(17.02d);
		source.add(95.12d);
		
		series.clear();
		
		assertEquals(0, source.getLength());
	}

}
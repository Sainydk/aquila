package ru.prolib.aquila.probe.internal;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.io.File;

import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.*;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventQueueStarter;
import ru.prolib.aquila.core.EventSystem;
import ru.prolib.aquila.core.EventSystemImpl;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.data.Aqiterator;
import ru.prolib.aquila.core.data.Tick;
import ru.prolib.aquila.probe.PROBETerminal;
import ru.prolib.aquila.probe.timeline.TLSTimeline;

public class XFactoryTest {
	private IMocksControl control;
	private XFactory x;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		x = new XFactory();
	}

	@Test
	public void testNewTerminal() throws Exception {
		PROBETerminal terminal = x.newTerminal("zulu24");
		assertNotNull(terminal);
	}
	
	@Test
	public void testNewQueueStarter() throws Exception {
		EventQueue queue = control.createMock(EventQueue.class);
		EventQueueStarter starter = x.newQueueStarter(queue, 2500L);
		assertNotNull(starter);
		assertEquals(2500L, starter.getTimeout());
		assertSame(queue, starter.getEventQueue());
	}
	
	@Test
	public void testNewSecurityHandlerFORTS() throws Exception {
		PROBETerminal terminal = control.createMock(PROBETerminal.class);
		EditableSecurity security = control.createMock(EditableSecurity.class);
		SecurityProperties props = control.createMock(SecurityProperties.class);
		SecurityHandlerFORTS handler =
				x.newSecurityHandlerFORTS(terminal, security, props);
		assertNotNull(handler);
		assertSame(terminal, handler.getTerminal());
		assertSame(security, handler.getSecurity());
		assertSame(props, handler.getSecurityProperties());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testNewTickDataDispatcher() throws Exception {
		Aqiterator<Tick> it = control.createMock(Aqiterator.class);
		TickHandler handler = control.createMock(TickHandler.class);
		TickDataDispatcher d = x.newTickDataDispatcher(it, handler);
		assertNotNull(d);
	}
	
	@Test
	public void testNewDataProvider() throws Exception {
		PROBETerminal terminal = control.createMock(PROBETerminal.class);
		DataProvider dp = x.newDataProvider(terminal);
		assertNotNull(dp);
	}
	
	@Test
	public void testNewDataStorage() throws Exception {
		File root = new File("fixture");
		PROBEDataStorage storage = x.newDataStorage(root);
		assertNotNull(storage);
	}
	
	@Test
	public void testNewTimeline() throws Exception {
		EventSystem es = new EventSystemImpl();
		Interval interval = new Interval(new DateTime(2014, 1, 1, 0, 0, 0, 0),
				new DateTime(2014, 12, 31, 23, 59, 59, 999));
		TLSTimeline tl = x.newTimeline(es, interval);
		assertNotNull(tl);
		assertEquals(interval, tl.getRunInterval());
	}
	
	@Test
	public void testNewScheduler() throws Exception {
		TLSTimeline timeline = control.createMock(TLSTimeline.class);
		SchedulerImpl scheduler = (SchedulerImpl) x.newScheduler(timeline);
		assertNotNull(scheduler);
	}

}

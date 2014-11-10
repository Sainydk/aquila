package ru.prolib.aquila.probe;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.easymock.IMocksControl;
import org.joda.time.*;
import org.junit.*;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.probe.internal.*;
import ru.prolib.aquila.probe.timeline.*;

public class PROBETerminalTest {
	private IMocksControl control;
	private PROBETerminal terminal;
	private TLSTimeline timeline;
	private EventType eventType;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		eventType = control.createMock(EventType.class);
		timeline = control.createMock(TLSTimeline.class);
		terminal = new PROBETerminal("foobar");
		terminal.getServiceLocator().setTimeline(timeline);
	}
	
	@Test
	public void testRequestSecurity() throws Exception {
		SecurityDescriptor descr = new SecurityDescriptor("foo", "bar", "RUR");
		fail("TODO: ");
		//control.replay();
		
		//terminal.requestSecurity(descr);
		
		//control.verify();
	}
	
	@Test
	public void testGetRunInterval() throws Exception {
		Interval i = new Interval(DateTime.now(), DateTime.now().plus(1));
		expect(timeline.getRunInterval()).andReturn(i);
		control.replay();
		
		assertEquals(i, terminal.getRunInterval());
		
		control.verify();
	}
	
	@Test
	public void testRunning() throws Exception {
		expect(timeline.running()).andReturn(true);
		expect(timeline.running()).andReturn(false);
		control.replay();
		
		assertTrue(terminal.running());
		assertFalse(terminal.running());
		
		control.verify();
	}
	
	@Test
	public void testPaused() throws Exception {
		expect(timeline.paused()).andReturn(false);
		expect(timeline.paused()).andReturn(true);
		control.replay();
		
		assertFalse(terminal.paused());
		assertTrue(terminal.paused());
		
		control.verify();
	}
	
	@Test
	public void testFinished() throws Exception {
		expect(timeline.finished()).andReturn(true);
		expect(timeline.finished()).andReturn(false);
		control.replay();
		
		assertTrue(terminal.finished());
		assertFalse(terminal.finished());
		
		control.verify();
	}
	
	@Test
	public void testFinish() throws Exception {
		timeline.finish();
		control.replay();
		
		terminal.finish();
		
		control.verify();
	}
	
	@Test
	public void testPause() throws Exception {
		timeline.pause();
		control.replay();
		
		terminal.pause();
		
		control.verify();
	}
	
	@Test
	public void testRunTo() throws Exception {
		DateTime t = DateTime.now();
		timeline.runTo(t);
		control.replay();
		
		terminal.runTo(t);
		
		control.verify();
	}
	
	@Test
	public void testRun() throws Exception {
		timeline.run();
		control.replay();
		
		terminal.run();
		
		control.verify();
	}

	@Test
	public void testOnFinish() throws Exception {
		expect(timeline.OnFinish()).andReturn(eventType);
		control.replay();
		
		assertSame(eventType, terminal.OnFinish());
		
		control.verify();
	}

	@Test
	public void testOnPause() throws Exception {
		expect(timeline.OnPause()).andReturn(eventType);
		control.replay();
		
		assertSame(eventType, terminal.OnPause());
		
		control.verify();
	}

	@Test
	public void testOnRun() throws Exception {
		expect(timeline.OnRun()).andReturn(eventType);
		control.replay();
		
		assertSame(eventType, terminal.OnRun());
		
		control.verify();
	}

}

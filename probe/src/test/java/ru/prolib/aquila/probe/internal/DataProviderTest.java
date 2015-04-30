package ru.prolib.aquila.probe.internal;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.*;
import org.apache.log4j.BasicConfigurator;
import org.easymock.*;
import org.joda.time.*;
import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.probe.*;
import ru.prolib.aquila.probe.timeline.*;

public class DataProviderTest {
	private static final SecurityDescriptor descr;
	
	static {
		descr = new SecurityDescriptor("AXE", "BLAH", "USD", SecurityType.BOND);
	}
	
	private IMocksControl control;
	private DataProvider dataProvider;
	private EditableTerminal underlyingTerminal;
	private PROBEServiceLocator locator;
	private PROBETerminal terminal;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		underlyingTerminal = control.createMock(EditableTerminal.class);
		locator = new PROBEServiceLocator();
		terminal = new PROBETerminal(underlyingTerminal, locator);
		dataProvider = new DataProvider();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testStartSupply() throws Exception {
		PROBEDataStorage storage = control.createMock(PROBEDataStorage.class);
		Aqiterator<Tick> iterator = control.createMock(Aqiterator.class);
		Timeline timeline = control.createMock(Timeline.class);
		SecurityProperties props = control.createMock(SecurityProperties.class);
		EditableSecurity security = control.createMock(EditableSecurity.class);
		
		DateTime time = DateTime.now();
		locator.setDataStorage(storage);
		locator.setTimeline(timeline);
		expect(storage.getIterator(descr, time)).andReturn(iterator);
		expect(underlyingTerminal.getEditableSecurity(descr))
			.andReturn(security);
		expect(storage.getSecurityProperties(descr)).andReturn(props);
		final Vector<TLEventSource> actual = new Vector<TLEventSource>();
		timeline.registerSource((TLEventSource) anyObject());
		expectLastCall().andAnswer(new IAnswer<Object>() {
			@Override public Object answer() throws Throwable {
				actual.add((TLEventSource) getCurrentArguments()[0]);
				return null;
			}});
		control.replay();
		
		dataProvider.startSupply(terminal, descr, time);
		
		control.verify();
		assertEquals(1, actual.size());
		TickDataDispatcher tdd = (TickDataDispatcher) actual.get(0);
		FORTSSecurityCtrl ctrl = (FORTSSecurityCtrl) tdd.getTickHandler();
		assertSame(terminal, ctrl.getTerminal());
		assertSame(security, ctrl.getSecurity());
		assertSame(props, ctrl.getSecurityProperties());
	}
	
}
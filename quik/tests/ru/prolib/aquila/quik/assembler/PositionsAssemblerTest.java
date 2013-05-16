package ru.prolib.aquila.quik.assembler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.util.*;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.quik.dde.*;

public class PositionsAssemblerTest {
	private IMocksControl control;
	private Cache cache;
	private PositionAssembler positionAssembler;
	private PositionsAssembler assembler;
	private EditableTerminal terminal;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		cache = control.createMock(Cache.class);
		positionAssembler = control.createMock(PositionAssembler.class);
		terminal = control.createMock(EditableTerminal.class);
		assembler = new PositionsAssembler(cache, positionAssembler);
		expect(positionAssembler.getTerminal()).andStubReturn(terminal);
	}
	
	@Test
	public void testOnEvent() throws Exception {
		PositionFCache c1 = control.createMock(PositionFCache.class),
			c2 = control.createMock(PositionFCache.class),
			c3 = control.createMock(PositionFCache.class);
		List<PositionFCache> list = new Vector<PositionFCache>();
		list.add(c1);
		list.add(c2);
		list.add(c3);
		expect(cache.getAllPositionsF()).andReturn(list);
		positionAssembler.adjustByCache(same(c1));
		positionAssembler.adjustByCache(same(c2));
		positionAssembler.adjustByCache(same(c3));
		control.replay();
		
		assembler.onEvent(null);
		
		control.verify();
	}

	@Test
	public void testStart() throws Exception {
		EventType type1 = control.createMock(EventType.class);
		EventType type2 = control.createMock(EventType.class);
		expect(cache.OnPositionsFCacheUpdate()).andStubReturn(type1);
		expect(terminal.OnSecurityAvailable()).andStubReturn(type2);
		type1.addListener(same(assembler));
		type2.addListener(same(assembler));
		control.replay();
		
		assembler.start();
		
		control.verify();
	}
	
	@Test
	public void testStop() throws Exception {
		EventType type1 = control.createMock(EventType.class);
		EventType type2 = control.createMock(EventType.class);
		expect(cache.OnPositionsFCacheUpdate()).andStubReturn(type1);
		expect(terminal.OnSecurityAvailable()).andStubReturn(type2);
		type2.removeListener(same(assembler));
		type1.removeListener(same(assembler));
		control.replay();
		
		assembler.stop();
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(assembler.equals(assembler));
		assertFalse(assembler.equals(null));
		assertFalse(assembler.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<Cache> vCache = new Variant<Cache>()
			.add(cache)
			.add(control.createMock(Cache.class));
		Variant<PositionAssembler> vAsm = new Variant<PositionAssembler>(vCache)
			.add(positionAssembler)
			.add(control.createMock(PositionAssembler.class));
		Variant<?> iterator = vAsm;
		int foundCnt = 0;
		PositionsAssembler found = null, x = null;
		do {
			x = new PositionsAssembler(vCache.get(), vAsm.get());
			if ( assembler.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(cache, found.getCache());
		assertSame(positionAssembler, found.getPositionAssembler());
	}

}
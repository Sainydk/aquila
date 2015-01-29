package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.OrderStatus;
import ru.prolib.aquila.core.utils.Variant;

public class OrderIsCancelledTest {
	private IMocksControl control;
	private OrderIsCancelled validator;
	private EditableOrder order;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		validator = new OrderIsCancelled();
		order = control.createMock(EditableOrder.class);
	}
	
	@Test
	public void testValidate_Ok() throws Exception {
		Variant<OrderStatus> vStatus = new Variant<OrderStatus>()
			.add(OrderStatus.ACTIVE)
			.add(OrderStatus.CANCEL_FAILED)
			.add(OrderStatus.CANCEL_SENT)
			.add(OrderStatus.CANCELLED)
			.add(OrderStatus.CONDITION)
			.add(OrderStatus.FILLED)
			.add(OrderStatus.PENDING)
			.add(OrderStatus.REJECTED)
			.add(OrderStatus.SENT);
		Variant<Boolean> vChanged = new Variant<Boolean>(vStatus)
			.add(true)
			.add(false);
		Variant<?> iterator = vChanged;
		int found = 0;
		int index = 0;
		do {
			setUp();
			String msg = "At #" + index;
			expect(order.hasChanged(EditableOrder.STATUS_CHANGED))
				.andStubReturn(vChanged.get());
			expect(order.getStatus()).andStubReturn(vStatus.get());
			control.replay();
			if ( vChanged.get() && vStatus.get() == OrderStatus.CANCELLED ) {
				found ++;
				assertTrue(msg, validator.validate(order));
				assertTrue(msg, order.hasChanged(EditableOrder.STATUS_CHANGED));
			} else {
				assertFalse(msg, validator.validate(order));
			}
			control.verify();
			index ++;
		} while ( iterator.next() );
		assertEquals(1, found);
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(validator.equals(validator));
		assertFalse(validator.equals(null));
		assertFalse(validator.equals(this));
		assertTrue(validator.equals(new OrderIsCancelled()));
	}

}
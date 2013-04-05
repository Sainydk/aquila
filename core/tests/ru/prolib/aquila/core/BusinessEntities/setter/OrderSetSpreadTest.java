package ru.prolib.aquila.core.BusinessEntities.setter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.Price;
import ru.prolib.aquila.core.BusinessEntities.PriceUnit;
import ru.prolib.aquila.core.BusinessEntities.setter.OrderSetSpread;

/**
 * 2012-10-25<br>
 * $Id: OrderSetSpreadTest.java 298 2012-10-27 16:07:51Z whirlwind $
 */
public class OrderSetSpreadTest {
	private static IMocksControl control;
	private static EditableOrder order;
	private static OrderSetSpread setter;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		order = control.createMock(EditableOrder.class);
		setter = new OrderSetSpread();
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testSet() throws Exception {
		Price price = new Price(PriceUnit.PERCENT, 1d);
		Object fixture[][] = {
				// value, expected value, set?
				{ 100500L,	null,	false },
				{ price,  	price,	true  },
				{ 201.1D,	null,	false },
				{ null,		null,	false },
				{ this,		null,	false },
				{ 123.456F,	null,	false },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			control.resetToStrict();
			if ( (Boolean) fixture[i][2] ) {
				order.setSpread((Price) fixture[i][1]);
			}
			control.replay();
			setter.set(order, fixture[i][0]);
			control.verify();
		}
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(setter.equals(setter));
		assertTrue(setter.equals(new OrderSetSpread()));
		assertFalse(setter.equals(null));
		assertFalse(setter.equals(this));
	}

}

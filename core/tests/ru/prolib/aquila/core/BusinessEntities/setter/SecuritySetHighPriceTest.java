package ru.prolib.aquila.core.BusinessEntities.setter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.setter.SecuritySetHighPrice;

/**
 * 2012-12-29<br>
 * $Id: SetSecurityHighPriceTest.java 388 2012-12-30 12:58:15Z whirlwind $
 */
public class SecuritySetHighPriceTest {
	private EditableSecurity security;
	private SecuritySetHighPrice setter;
	private IMocksControl control;

	@Before
	public void setUp() throws Exception {
		setter = new SecuritySetHighPrice();
	}

	@Test
	public void testSet() throws Exception {
		Object fixture[][] = {
				// value, expected value, set?
				{ new Double(88.5514d),	88.5514d, true  },
				{ new Integer(999),		999.00d,  true  },
				{ new Long(182),		182.00d,  true  },
				{ null,					null,     true  },
				{ new Boolean(false),	null,     false },
				{ this,					null,     false },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			control = createStrictControl();
			security = createMock(EditableSecurity.class);
			if ( (Boolean) fixture[i][2] ) {
				security.setHighPrice((Double) fixture[i][1]);
			}
			control.replay();
			setter.set(security, fixture[i][0]);
			control.verify();
		}
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(setter.equals(setter));
		assertTrue(setter.equals(new SecuritySetHighPrice()));
		assertFalse(setter.equals(null));
		assertFalse(setter.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(20121229, 154357)
			.append(SecuritySetHighPrice.class)
			.toHashCode(), setter.hashCode());
	}

}

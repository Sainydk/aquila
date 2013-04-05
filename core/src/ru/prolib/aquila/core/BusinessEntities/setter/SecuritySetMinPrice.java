package ru.prolib.aquila.core.BusinessEntities.setter;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.data.S;

/**
 * Сеттер нижнего лимита цены.
 * <p>
 * 2012-08-12<br>
 * $Id: SetSecurityMinPrice.java 252 2012-08-12 16:51:42Z whirlwind $
 */
public class SecuritySetMinPrice implements S<EditableSecurity> {
	
	/**
	 * Создать сеттер.
	 */
	public SecuritySetMinPrice() {
		super();
	}

	/**
	 * Установить нижний лимит цены.
	 * <p>
	 * Допустимые типы значений {@link java.lang.Double} или null.
	 */
	@Override
	public void set(EditableSecurity security, Object value) {
		if ( value == null || value.getClass() == Double.class ) {
			security.setMinPrice((Double) value);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == SecuritySetMinPrice.class;
	}

}

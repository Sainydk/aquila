package ru.prolib.aquila.core.BusinessEntities.setter;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.data.S;

/**
 * Сеттер биржевого номера заявки.
 * <p>
 * 2012-09-26<br>
 * $Id: OrderSetId.java 301 2012-11-04 01:37:17Z whirlwind $
 */
public class OrderSetId implements S<EditableOrder> {
	
	/**
	 * Создать сеттер.
	 */
	public OrderSetId() {
		super();
	}

	/**
	 * Установить биржевой номер заявки.
	 * <p>
	 * Допустимые значение типа {@link java.lang.Long},
	 * {@link java.lang.Integer}, {@link java.lang.Double} и
	 * {@link java.lang.Float}. Вещественные значения приводятся к типу
	 * Long с потерей точности.   
	 */
	@Override
	public void set(EditableOrder object, Object value) {
		if ( value != null ) {
			Class<?> valueClass = value.getClass();
			if ( valueClass == Long.class ) {
				object.setId((Long) value);
			} else if ( valueClass == Integer.class ) {
				object.setId(((Integer) value).longValue());
			} else if ( valueClass == Double.class ) {
				object.setId(((Double) value).longValue());
			} else if ( valueClass == Float.class ) {
				object.setId(((Float) value).longValue());
			}
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other != null && other.getClass() == OrderSetId.class ) {
			return true;
		} else {
			return false;
		}
	}

}

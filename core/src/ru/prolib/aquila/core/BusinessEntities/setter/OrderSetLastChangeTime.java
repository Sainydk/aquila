package ru.prolib.aquila.core.BusinessEntities.setter;

import java.util.Date;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.data.S;

/**
 * Сеттер времени последнего изменения заявки.
 * <p>
 * 2013-02-21<br>
 * $Id: OrderSetLastChangeTime.java 542 2013-02-23 04:15:34Z whirlwind $
 */
public class OrderSetLastChangeTime implements S<EditableOrder> {
	
	public OrderSetLastChangeTime() {
		super();
	}

	@Override
	public void set(EditableOrder object, Object value) {
		if ( value != null && value.getClass() == Date.class ) {
			object.setLastChangeTime((Date) value);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		return other != null
			&& other.getClass() == OrderSetLastChangeTime.class;
	}

}

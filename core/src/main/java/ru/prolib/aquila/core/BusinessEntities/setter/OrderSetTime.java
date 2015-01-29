package ru.prolib.aquila.core.BusinessEntities.setter;

import org.joda.time.DateTime;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Сеттер времени выставления заявки.
 * <p>
 * 2013-02-21<br>
 * $Id: OrderSetTime.java 542 2013-02-23 04:15:34Z whirlwind $
 */
public class OrderSetTime implements S<EditableOrder> {
	
	public OrderSetTime() {
		super();
	}

	@Override
	public void set(EditableOrder object, Object value) throws ValueException {
		if ( value != null && value.getClass() == DateTime.class ) {
			object.setTime((DateTime) value);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		return other != null && other.getClass() == OrderSetTime.class;
	}

}
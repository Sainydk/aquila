package ru.prolib.aquila.core.BusinessEntities.validator;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.OrderImpl;
import ru.prolib.aquila.core.BusinessEntities.OrderStatus;
import ru.prolib.aquila.core.utils.Validator;
import ru.prolib.aquila.core.utils.ValidatorException;

/**
 * Валидатор определения неудачи процесса отмены заявки.
 * <p>
 * 2012-09-23<br>
 * $Id: OrderIsCancelFailed.java 287 2012-10-15 03:30:51Z whirlwind $
 */
public class OrderIsCancelFailed implements Validator {
	
	/**
	 * Конструктор.
	 */
	public OrderIsCancelFailed() {
		super();
	}

	@Override
	public boolean validate(Object object) throws ValidatorException {
		if ( object instanceof EditableOrder ) {
			EditableOrder order = (EditableOrder) object;
			return order.hasChanged(OrderImpl.STATUS_CHANGED)
				&& order.getStatus() == OrderStatus.CANCEL_FAILED;
		}
		return false;
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == OrderIsCancelFailed.class;
	}

}

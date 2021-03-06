package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.EventType;

/**
 * Order execution event.
 */
public class OrderExecutionEvent extends OrderEvent {
	private final OrderExecution execution;

	public OrderExecutionEvent(EventType type, Order order, Instant time, OrderExecution execution) {
		super(type, order, time);
		this.execution = execution;
	}
	
	public OrderExecution getExecution() {
		return execution;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null ) {
			return false;
		}
		if ( other.getClass() != OrderExecutionEvent.class ) {
			return false;
		}
		OrderExecutionEvent o = (OrderExecutionEvent) other;
		return new EqualsBuilder()
			.append(getType(), o.getType())
			.append(getOrder(), o.getOrder())
			.append(getTime(), o.getTime())
			.append(execution, o.execution)
			.isEquals();
	}

}

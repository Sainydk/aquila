package ru.prolib.aquila.probe.timeline;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.joda.time.DateTime;

/**
 * Событие хронологии.
 * <p>
 * Событие хронологии - это произвольная процедура, связанная с определенной
 * временной меткой. Однажды исполненное событие не может быть выполнено
 * повторно.
 */
public class TLEvent {
	protected final DateTime time;
	protected final Runnable procedure;
	private boolean executed = false;

	/**
	 * Конструктор.
	 * <p>
	 * @param time время наступления события
	 * @param procedure процедура исполнения события
	 */
	public TLEvent(DateTime time, Runnable procedure) {
		super();
		this.time = time;
		this.procedure = procedure;
	}
	
	/**
	 * Получить время наступления события.
	 * <p>
	 * @return  время события
	 */
	public DateTime getTime() {
		return time;
	}
	
	/**
	 * Получить процедуру исполнения события.
	 * <p>
	 * @return  процедура
	 */
	public Runnable getProcedure() {
		return procedure;
	}

	/**
	 * Исполнить событие.
	 * <p>
	 * Если событие уже было выполнено, то ничего не происходит.
	 */
	public synchronized void execute() {
		if ( ! executed ) {
			procedure.run();
			executed = true;
		}
	}
	
	/**
	 * Проверить факт исполнения события.
	 * <p>
	 * @return true - событие исполнено, false - не исполнено
	 */
	public synchronized boolean executed() {
		return executed;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName()
			+ "[" + time + " for " + procedure + "]";
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( this == other ) {
			return true;
		}
		if ( other == null || other.getClass() != TLEvent.class ) {
			return false;
		}
		TLEvent o = (TLEvent) other;
		return new EqualsBuilder()
			.append(o.executed, executed)
			.append(o.time, time)
			.append(o.procedure == procedure, true)
			.isEquals();
	}

}

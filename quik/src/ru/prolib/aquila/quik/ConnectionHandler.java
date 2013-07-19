package ru.prolib.aquila.quik;

import org.apache.commons.lang3.builder.EqualsBuilder;
import ru.prolib.aquila.core.*;

/**
 * Обработчик соединения с API.
 * <p>
 * Выполняет своевременное подключение, отключение и восстановление соединения.
 */
public class ConnectionHandler implements Starter, EventListener {
	private final QUIKEditableTerminal terminal;
	private final QUIKConfig config;
	
	/**
	 * Служебный конструктор.
	 * <p>
	 * @param terminal экземпляр терминала
	 * @param config параметры подключения
	 */
	ConnectionHandler(QUIKEditableTerminal terminal, QUIKConfig config) {
		super();
		this.terminal = terminal;
		this.config = config;
	}
	
	QUIKEditableTerminal getTerminal() {
		return terminal;
	}
	
	QUIKConfig getConfig() {
		return config;
	}

	@Override
	public void start() {
		terminal.OnStarted().addListener(this);
		terminal.OnStopped().addListener(this);
		terminal.OnDisconnected().addListener(this);
	}

	@Override
	public void stop() {
		terminal.OnStarted().removeListener(this);
		terminal.OnStopped().removeListener(this);
		terminal.OnDisconnected().removeListener(this);
		terminal.getClient().disconnect();
	}

	@Override
	public void onEvent(Event event) {
		if ( event.isType(terminal.OnStarted()) ) {
			terminal.schedule(new DoReconnect(terminal, config), 0L, 5000L);
		} else if ( event.isType(terminal.OnDisconnected()) ) {
			if ( terminal.started() ) {
				terminal.schedule(new DoReconnect(terminal, config), 0L, 5000L);
			}
		
		} else if ( event.isType(terminal.OnStopped()) ) {
			terminal.getClient().disconnect();
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != ConnectionHandler.class ) {
			return false;
		}
		ConnectionHandler o = (ConnectionHandler) other;
		return new EqualsBuilder()
			.append(o.config, config)
			.appendSuper(o.terminal == terminal)
			.isEquals();
	}

}

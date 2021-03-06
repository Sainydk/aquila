package ru.prolib.aquila.quik;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.quik.api.QUIKClient;
import ru.prolib.aquila.quik.assembler.cache.Cache;

/**
 * QUIK terminal implementation.
 */
public class QUIKTerminal extends TerminalImpl {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(QUIKTerminal.class);
	}
	
	private final QUIKServiceLocator locator;

	public QUIKTerminal(TerminalParams params, QUIKServiceLocator locator) {
		super(params);
		this.locator = locator;
	}
	
	public QUIKServiceLocator getServiceLocator() {
		return locator;
	}

	/**
	 * Получить кэш данных.
	 * <p>
	 * @return кэш данных
	 */
	public Cache getDataCache() {
		return getServiceLocator().getDataCache();
	}

	/**
	 * Получить API-подключение.
	 * <p> 
	 * @return объект подключения
	 */
	public QUIKClient getClient() {
		return getServiceLocator().getClient();
	}
	
	@Override
	public void requestSecurity(Symbol symbol) {
		logger.warn("TODO: requestSecurity() not implemented");
	}

}

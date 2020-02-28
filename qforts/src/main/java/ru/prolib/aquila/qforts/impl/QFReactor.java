package ru.prolib.aquila.qforts.impl;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.OrderException;
import ru.prolib.aquila.core.BusinessEntities.SPRunnable;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.SecurityEvent;
import ru.prolib.aquila.core.BusinessEntities.SecurityField;
import ru.prolib.aquila.core.BusinessEntities.SecurityTickEvent;
import ru.prolib.aquila.core.BusinessEntities.SubscrHandler;
import ru.prolib.aquila.core.BusinessEntities.SubscrHandlerStub;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.SymbolSubscrRepository;
import ru.prolib.aquila.core.BusinessEntities.TaskHandler;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.data.DataProvider;

public class QFReactor implements EventListener, DataProvider, SPRunnable {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(QFReactor.class);
	}
	
	private final QForts facade;
	private final AtomicLong seqOrderID;
	private final QFSessionSchedule schedule;
	private final QFSymbolDataService symbolDataService;
	private EditableTerminal terminal;
	private TaskHandler taskHandler;
	
	public QFReactor(QForts facade,
					 QFObjectRegistry registry,
					 QFSessionSchedule schedule,
					 AtomicLong seqOrderID,
					 QFSymbolDataService symbol_data_service)
	{
		this.facade = facade;
		this.schedule = schedule;
		this.seqOrderID = seqOrderID;
		this.symbolDataService = symbol_data_service;
	}
	
	// This is bad idea. Gain an access to this object thru builder -> app context -> get bean
	@Deprecated
	synchronized public SymbolSubscrRepository getSymbolSubscrRepository() {
		return symbolDataService.getSymbolSubscrRepository();
	}
	
	/**
	 * For testing purposes only.
	 * <p>
	 * @param terminal instance
	 */
	synchronized void setTerminal(EditableTerminal terminal) {
		this.terminal = terminal;
	}
	
	/**
	 * For testing purposes only.
	 * <p>
	 * @return terminal instance
	 */
	synchronized EditableTerminal getTerminal() {
		return terminal;
	}
	
	/**
	 * For testing purposes only.
	 * <p>
	 * @param handler instance
	 */
	synchronized void setTaskHandler(TaskHandler handler) {
		this.taskHandler = handler;
	}
	
	/**
	 * For testing purposes only.
	 * <p>
	 * @return handler instance
	 */
	synchronized TaskHandler getTaskHandler() {
		return taskHandler;
	}

	@Override
	synchronized public void run() {
		EditableTerminal terminal = null;
		synchronized ( this ) {
			terminal = this.terminal;
		}
		if ( terminal == null ) {
			return;
		}
		Instant currentTime = terminal.getCurrentTime();

		try {
			switch ( schedule.getCurrentProc(currentTime) ) {
			case UPDATE_BY_MARKET:
				facade.updateByMarket();
				break;
			case MID_CLEARING:
				facade.midClearing();
				logger.debug("[I] Clearing finished @" + currentTime);
				break;
			case CLEARING:
				facade.clearing();
				logger.debug("[M] Clearing finished @" + currentTime);
				break;
			}
		} catch ( QFTransactionException e ) {
			logger.error("Unexpected exception: ", e);
		}
	}

	@Override
	synchronized public Instant getNextExecutionTime(Instant currentTime) {
		return schedule.getNextRunTime(currentTime);
	}

	@Override
	synchronized public boolean isLongTermTask() {
		return false;
	}

	@Override
	synchronized public long getNextOrderID() {
		return seqOrderID.incrementAndGet();
	}

	@Override
	synchronized public void subscribeRemoteObjects(EditableTerminal terminal) {
		synchronized ( this ) {
			if ( this.terminal != null ) {
				throw new IllegalStateException();
			}
			this.terminal = terminal;
		}
		terminal.onSecurityUpdate().addListener(this);
		terminal.onSecurityLastTrade().addListener(this);
		symbolDataService.setTerminal(terminal);
		symbolDataService.onConnectionStatusChange(true);
		taskHandler = terminal.schedule(this);
		logger.debug("Reactor started for: {}", terminal.getTerminalID());
	}

	@Override
	synchronized public void unsubscribeRemoteObjects(EditableTerminal terminal) {
		TaskHandler th = null;
		synchronized ( this ) {
			if ( this.terminal != terminal ) {
				throw new IllegalStateException();
			}
			this.terminal = null;
			th = taskHandler;
			taskHandler = null;
		}
		terminal.onSecurityUpdate().removeListener(this);
		terminal.onSecurityLastTrade().removeListener(this);
		symbolDataService.onConnectionStatusChange(false);
		th.cancel();
		logger.debug("Reactor stopped for: {}", terminal.getTerminalID());
	}

	@Override
	synchronized public void registerNewOrder(EditableOrder order) throws OrderException {
		try {
			facade.registerOrder(order);
		} catch ( QFTransactionException e ) {
			throw new OrderException(e);
		}
	}

	@Override
	synchronized public void cancelOrder(EditableOrder order) throws OrderException {
		try {
			facade.cancelOrder(order);
		} catch ( QFTransactionException e ) {
			throw new OrderException(e);
		}
	}

	@Override
	synchronized public void onEvent(Event event) {
		if ( event instanceof SecurityEvent ) {
			Security security = ((SecurityEvent) event).getSecurity();
			Terminal terminal = security.getTerminal();
			if ( event.isType(terminal.onSecurityLastTrade()) ) {
				onSecurityTradeEvent((SecurityTickEvent) event);			
			} else if ( event.isType(terminal.onSecurityUpdate()) ) {
				onSecurityUpdateEvent((SecurityEvent) event);
			}
		}
	}
	
	private void onSecurityTradeEvent(SecurityTickEvent event) {
		Tick tick = event.getTick();
		try {
			facade.handleOrders(event.getSecurity(), tick.getSize(), tick.getPrice());
		} catch ( QFTransactionException e ) {
			logger.error("Unexpected exception: ", e);
		}
	}
	
	private void onSecurityUpdateEvent(SecurityEvent event) {
		if ( ! event.hasChanged(SecurityField.INITIAL_MARGIN) ) {
			return;
		}
		Security security = event.getSecurity();
		switch ( schedule.getCurrentPeriod(security.getTerminal().getCurrentTime()) ) {
		case QFSessionSchedule.PCVM1_1:
		case QFSessionSchedule.PCVM1_2:
		case QFSessionSchedule.PCVM2:
			try {
				facade.updateMargin(security);
			} catch ( QFTransactionException e ) {
				logger.error("Unexpected exception: ", e);
			}
			break;
		}
	}

	@Override
	synchronized public SubscrHandler subscribe(Symbol symbol, MDLevel level, EditableTerminal terminal) {
		symbolDataService.setTerminal(terminal);
		return symbolDataService.onSubscribe(symbol, level);
	}

	@Override
	synchronized public SubscrHandler subscribe(Account account, EditableTerminal terminal) {
		EditablePortfolio portfolio = null;
		terminal.lock();
		try {
			if ( terminal.isPortfolioExists(account) ) {
				return new SubscrHandlerStub();
			}
			portfolio = terminal.getEditablePortfolio(account);
		} finally {
			terminal.unlock();
		}
		facade.registerPortfolio(portfolio);
		try {
			facade.changeBalance(portfolio, CDecimalBD.ofRUB2("1000000.00"));
		} catch ( QFTransactionException e ) {
			throw new IllegalStateException("Unable to change account balance: " + account, e);
		}
		return new SubscrHandlerStub();
	}

	@Override
	synchronized public void close() {
		
	}

}

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
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
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
import ru.prolib.aquila.core.BusinessEntities.SymbolSubscrCounter;
import ru.prolib.aquila.core.BusinessEntities.SymbolSubscrRepository;
import ru.prolib.aquila.core.BusinessEntities.TaskHandler;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.data.DataProvider;
import ru.prolib.aquila.data.DataSource;

public class QFReactor implements EventListener, DataProvider, SPRunnable {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(QFReactor.class);
	}
	
	private final QForts facade;
	private final QFObjectRegistry registry;
	private final AtomicLong seqOrderID;
	private final QFSessionSchedule schedule;
	private final DataSource dataSource;
	private final SymbolSubscrRepository symbolSubs;
	private EditableTerminal terminal;
	private TaskHandler taskHandler;
	
	public QFReactor(QForts facade,
					 QFObjectRegistry registry,
					 QFSessionSchedule schedule,
					 AtomicLong seqOrderID,
					 DataSource dataSource,
					 SymbolSubscrRepository symbolSubs)
	{
		this.facade = facade;
		this.registry = registry;
		this.schedule = schedule;
		this.seqOrderID = seqOrderID;
		this.dataSource = dataSource;
		this.symbolSubs = symbolSubs;
	}
	
	/**
	 * For testing purposes only.
	 * <p>
	 * @param terminal instance
	 */
	void setTerminal(EditableTerminal terminal) {
		this.terminal = terminal;
	}
	
	/**
	 * For testing purposes only.
	 * <p>
	 * @return terminal instance
	 */
	EditableTerminal getTerminal() {
		return terminal;
	}
	
	/**
	 * For testing purposes only.
	 * <p>
	 * @param handler instance
	 */
	void setTaskHandler(TaskHandler handler) {
		this.taskHandler = handler;
	}
	
	/**
	 * For testing purposes only.
	 * <p>
	 * @return handler instance
	 */
	TaskHandler getTaskHandler() {
		return taskHandler;
	}

	@Override
	public void run() {
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
	public Instant getNextExecutionTime(Instant currentTime) {
		return schedule.getNextRunTime(currentTime);
	}

	@Override
	public boolean isLongTermTask() {
		return false;
	}

	@Override
	public long getNextOrderID() {
		return seqOrderID.incrementAndGet();
	}

	@Override
	public void subscribeRemoteObjects(EditableTerminal terminal) {
		synchronized ( this ) {
			if ( this.terminal != null ) {
				throw new IllegalStateException();
			}
			this.terminal = terminal;
		}
		terminal.onSecurityUpdate().addListener(this);
		terminal.onSecurityLastTrade().addListener(this);
		taskHandler = terminal.schedule(this);
		logger.debug("Reactor started for: {}", terminal.getTerminalID());
	}

	@Override
	public void unsubscribeRemoteObjects(EditableTerminal terminal) {
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
		th.cancel();
		logger.debug("Reactor stopped for: {}", terminal.getTerminalID());
	}

	@Override
	public void registerNewOrder(EditableOrder order) throws OrderException {
		try {
			facade.registerOrder(order);
		} catch ( QFTransactionException e ) {
			throw new OrderException(e);
		}
	}

	@Override
	public void cancelOrder(EditableOrder order) throws OrderException {
		try {
			facade.cancelOrder(order);
		} catch ( QFTransactionException e ) {
			throw new OrderException(e);
		}
	}

	@Override
	public void onEvent(Event event) {
		if ( event instanceof SecurityEvent ) {
			Security security = ((SecurityEvent) event).getSecurity();
			if ( ! registry.isRegistered(security) ) {
				return;
			}
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
	public SubscrHandler subscribe(Symbol symbol, MDLevel level, EditableTerminal terminal) {
		boolean doit = false;
		SymbolSubscrCounter subs = null;
		EditableSecurity security = terminal.getEditableSecurity(symbol);
		symbolSubs.lock();
		try {
			subs = symbolSubs.subscribe(symbol, level);
			subs.lock();
		} finally {
			symbolSubs.unlock();
		}
		try {
			if ( subs.getNumL0() == 1 ) {
				doit = true;
			}
		} finally {
			subs.unlock();
		}
		if ( doit ) {
			facade.registerSecurity(security);
			dataSource.subscribeL1(symbol, security);
			dataSource.subscribeMD(symbol, security);
			dataSource.subscribeSymbol(symbol, security);
		}
		return new QFSymbolSubscrHandler(this, security, level);
	}
	
	void unsubscribe(EditableSecurity security, MDLevel level) {
		boolean doit = false;
		SymbolSubscrCounter subs = null;
		Symbol symbol = security.getSymbol();
		symbolSubs.lock();
		try {
			subs = symbolSubs.unsubscribe(symbol, level);
			subs.lock();
		} finally {
			symbolSubs.unlock();
		}
		try {
			if ( subs.getNumL0() == 0 ) {
				doit = true;
			}
		} finally {
			subs.unlock();
		}
		if ( doit ) {
			dataSource.unsubscribeL1(symbol, security);
			dataSource.unsubscribeMD(symbol, security);
			dataSource.unsubscribeSymbol(symbol, security);
		}
	}

	@Override
	public SubscrHandler subscribe(Account account, EditableTerminal terminal) {
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
	public void close() {
		
	}

}

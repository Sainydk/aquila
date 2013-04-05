package ru.prolib.aquila.core.BusinessEntities.utils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;

/**
 * Контроллер терминала.
 * <p>
 * Выполняет запуск процедур старта/останова терминала в отдельном потоке.
 * <p>
 * 2013-02-10<br>
 * $Id$
 */
public class TerminalController {
	private static final Logger logger;
	private final TerminalControllerHelper helper;
	
	static {
		logger = LoggerFactory.getLogger(TerminalController.class);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * Создает экземпляр помощьника по-умолчанию.
	 */
	public TerminalController() {
		this(new TerminalControllerHelper());
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param helper помощник контроллера
	 */
	public TerminalController(TerminalControllerHelper helper) {
		super();
		this.helper = helper;
	}
	
	/**
	 * Получить помощника контроллера.
	 * <p>
	 * @return помощник контроллер
	 */
	public TerminalControllerHelper getHelper() {
		return helper;
	}

	/**
	 * Запустить процедуру запуска терминала.
	 * <p>
	 * @param terminal целевой терминал
	 */
	public void runStartSequence(EditableTerminal terminal) {
		CountDownLatch sig = helper.createStartedSignal();
		helper.createThread(helper.createStartSequence(terminal, sig)).start();
		try {
			if ( ! sig.await(1000, TimeUnit.MILLISECONDS) ) {
				logger.warn("runStartSequence: Timeout during start sequence");
			}
		} catch ( InterruptedException e ) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Запустить процедуру останова терминала.
	 * <p>
	 * @param terminal целевой терминал
	 */
	public void runStopSequence(EditableTerminal terminal) {
		CountDownLatch sig = helper.createStartedSignal();
		helper.createThread(helper.createStopSequence(terminal, sig)).start();
		try {
			if ( ! sig.await(1000, TimeUnit.MILLISECONDS) ) {
				logger.warn("runStopSequence: Timeout during start sequence");
			}
		} catch ( InterruptedException e ) {
			throw new RuntimeException(e);
		}
	}

}

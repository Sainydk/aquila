package ru.prolib.aquila.data.storage;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;
import ru.prolib.aquila.core.SimpleEventFactory;
import ru.prolib.aquila.core.BusinessEntities.L1UpdateImpl;
import ru.prolib.aquila.core.BusinessEntities.SecurityTickEvent;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.data.storage.file.SimpleCsvL1UpdateWriterFactory;

public class SimpleL1Recorder implements EventListener {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(SimpleL1Recorder.class);
	}
	
	private final Terminal terminal;
	private final Lock lock;
	private final EventQueue queue;
	private final EventType onStarted, onStopped;
	private final L1UpdateWriterFactory writerFactory;
	private boolean started = false;
	private L1UpdateWriter writer;
	
	public SimpleL1Recorder(EventQueue queue, Terminal terminal, L1UpdateWriterFactory writerFactory) {
		super();
		this.lock = new ReentrantLock();
		this.terminal = terminal;
		this.queue = queue;
		this.onStarted = new EventTypeImpl("STARTED");
		this.onStopped = new EventTypeImpl("STOPPED");
		this.writerFactory = writerFactory;
	}
	
	public SimpleL1Recorder(EventQueue queue, Terminal terminal, File file) {
		this(queue, terminal, new SimpleCsvL1UpdateWriterFactory(file));
	}
	
	public EventQueue getEventQueue() {
		return queue;
	}
	
	public Terminal getTerminal() {
		return terminal;
	}
	
	public L1UpdateWriterFactory getWriterFactory() {
		return writerFactory;
	}
	
	public EventType onStarted() {
		return onStarted;
	}
	
	public EventType onStopped() {
		return onStopped;
	}
	
	public void close() {
		stopWritingUpdates();
		onStarted.removeAlternates();
		onStarted.removeListeners();
		onStopped.removeAlternates();
		onStopped.removeListeners();
	}
	
	public boolean isStarted() {
		lock.lock();
		try {
			return started;
		} finally {
			lock.unlock();
		}
	}
	
	public void startWritingUpdates() throws IOException {
		lock.lock();
		try {
			if ( started ) {
				throw new IllegalStateException("Already started");
			}
			writer = writerFactory.createWriter();
			terminal.onSecurityBestAsk().addListener(this);
			terminal.onSecurityBestBid().addListener(this);
			terminal.onSecurityLastTrade().addListener(this);
			started = true;
			queue.enqueue(onStarted, new SimpleEventFactory());

		} finally {
			lock.unlock();
		}
	}
	
	public void stopWritingUpdates() {
		lock.lock();
		try {
			if ( started ) {
				terminal.onSecurityBestAsk().removeListener(this);
				terminal.onSecurityBestBid().removeListener(this);
				terminal.onSecurityLastTrade().removeListener(this);
				if ( writer != null ) {
					try {
						writer.close();
					} catch ( IOException e ) {
						logger.warn("Unexpected exception: ", e);
					}
					writer = null;
				}
				started = false;
				queue.enqueue(onStopped, new SimpleEventFactory());
			}
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void onEvent(Event event) {
		lock.lock();
		try {
			if ( started ) {
				SecurityTickEvent e = (SecurityTickEvent) event;
				writer.writeUpdate(new L1UpdateImpl(e.getSecurity()
						.getSymbol(), e.getTick()));
			}
		} catch ( IOException e ) {
			logger.error("Stop recording: ", e);
			stopWritingUpdates();
		} finally {
			lock.unlock();
		}
	}

}

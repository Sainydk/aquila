package ru.prolib.aquila.core.eque;

import java.util.Set;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.EventType;

public class DispatcherThreadV3 extends Thread {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(DispatcherThreadV3.class);
	}
	
	public static DispatcherThreadV3
		createV3(BlockingQueue<EventDispatchingRequest> queue,
				String queueName,
				EventQueueStats stats)
	{
		DispatcherThreadV3 t = new DispatcherThreadV3(queue, stats);
		t.setName(queueName + ".DISPATCHER");
		t.setDaemon(true);
		return t;
	}
	
	private final BlockingQueue<EventDispatchingRequest> queue;
	private final EventQueueStats stats;
	
	public DispatcherThreadV3(BlockingQueue<EventDispatchingRequest> queue, EventQueueStats stats) {
		this.queue = queue;
		this.stats = stats;
	}

	@Override
	public void run() {
		EventDispatchingRequest request;
		try {
			while ( (request = queue.take()) != null ) {
				if ( request == EventDispatchingRequest.EXIT ) {
					break;
				}
				//long b1 = System.nanoTime();
				Set<EventType> types = request.type.getFullListOfRelatedTypes();
				//long b2 = System.nanoTime();
				for ( EventType type : types ) {
					Event event = request.factory.produceEvent(type);
					for ( EventListener listener : type.getListeners() ) {
						try {
							listener.onEvent(event);
						} catch ( Throwable t ) {
							logger.error("Error dispatching event {} to {}", event, listener);
							logger.error("Unhandled exception: ", t);
						}
					}
				}
				//stats.addDispatchingTime(System.nanoTime() - b2);
				//stats.addBuildingTaskListTime(b2 - b1);
				stats.addEventDispatched();
			}
		} catch ( InterruptedException e ) {
			logger.error("Interrupted: ", e);
			Thread.currentThread().interrupt();
		}
	}
	
}

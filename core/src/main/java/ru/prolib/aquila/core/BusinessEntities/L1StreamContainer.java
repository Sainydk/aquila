package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.EventType;

public interface L1StreamContainer extends AbstractContainer {
	
	public EventType onBestBid();
	
	public EventType onBestAsk();
	
	public EventType onLastTrade();
	
	public Tick getBestBid();
	
	public Tick getBestAsk();
	
	public Tick getLastTrade();

}

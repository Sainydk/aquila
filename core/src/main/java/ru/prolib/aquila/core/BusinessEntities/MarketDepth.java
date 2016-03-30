package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;

public class MarketDepth {
	private static final String SEP = "#";
	private final Symbol symbol;
	private final List<Tick> asks, bids;
	private final Instant time;
	private final int scale;
	private final DoubleUtils doubleUtils;
	
	public MarketDepth(Symbol symbol, List<Tick> asks, List<Tick> bids, Instant time, int scale) {
		super();
		this.symbol = symbol;
		this.asks = new ArrayList<Tick>(asks);
		this.bids = new ArrayList<Tick>(bids);
		this.time = time;
		this.scale = scale;
		this.doubleUtils = new DoubleUtils(scale);
	}
	
	public Symbol getSymbol() {
		return symbol;
	}
	
	public long getTimestamp() {
		return time.toEpochMilli();
	}
	
	public List<Tick> getAsks() {
		return asks;
	}
	
	public List<Tick> getBids() {
		return bids;
	}

	public boolean hasBestAsk() {
		return asks.size() > 0;
	}
	
	public boolean hasAskAtOffset(int recordOffset) {
		return recordOffset < asks.size();
	}
	
	public Tick getBestAsk() throws MarketDepthException {
		if ( hasBestAsk() ) {
			return asks.get(0);
		}
		throw new MarketDepthException("No ask available: " + symbol + SEP + "0");
	}
	
	public Tick getAskAtOffset(int recordOffset) throws MarketDepthException {
		if ( hasAskAtOffset(recordOffset) ) {
			return asks.get(recordOffset);
		}
		throw new MarketDepthException("No ask available: " + symbol + SEP + recordOffset);
	}
	
	public boolean hasBestBid() {
		return bids.size() > 0;
	}
	
	public boolean hasBidAtOffset(int recordOffset) {
		return recordOffset < bids.size();
	}
	
	public Tick getBestBid() throws MarketDepthException {
		if ( hasBestBid() ) {
			return bids.get(0);
		}
		throw new MarketDepthException("No bid available: " + symbol + SEP + "0");
	}
	
	public Tick getBidAtOffset(int recordOffset) throws MarketDepthException {
		if ( hasBidAtOffset(recordOffset) ) {
			return bids.get(recordOffset);
		}
		throw new MarketDepthException("No bid available: " + symbol + SEP + recordOffset);
	}
	
	public boolean hasAskAtPriceLevel(double priceLevel) {
		return findWithPrice(priceLevel, asks) != null;
	}
	
	public long getAskAtPriceLevel(double priceLevel) {
		Tick tick = findWithPrice(priceLevel, asks);
		return tick == null ? 0 : tick.getSize();
	}
	
	public boolean hasBidAtPriceLevel(double priceLevel) {
		return findWithPrice(priceLevel, bids) != null;
	}
	
	public long getBidAtPriceLevel(double priceLevel) {
		Tick tick = findWithPrice(priceLevel, bids);
		return tick == null ? 0 : tick.getSize();
	}
	
	private Tick findWithPrice(double price, List<Tick> ticks) {
		for ( Tick tick : ticks ) {
			if ( doubleUtils.isEquals(tick.getPrice(), price) ) {
				return tick;
			}
		}
		return null;
	}
	
	public int getBidCount() {
		return bids.size();
	}
	
	public int getAskCount() {
		return asks.size();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != MarketDepth.class ) {
			return false;
		}
		MarketDepth o = (MarketDepth) other;
		return new EqualsBuilder()
			.append(symbol, o.symbol)
			.append(time, o.time)
			.append(scale, o.scale)
			.append(asks, o.asks)
			.append(bids, o.bids)
			.isEquals();
	}
	
	@Override
	public String toString() {
		return "MarketDepth[" + symbol + " at " + getTime() + "\n"
				+ " asks[" + StringUtils.join(asks, "\n,") + "]\n"
				+ " bids[" + StringUtils.join(bids, "\n,")
				+ " scale=" + scale + "]";
	}
	
	public Instant getTime() {
		return time;
	}
	
	public double roundPrice(double x) {
		return doubleUtils.round(x);
	}

}

package ru.prolib.aquila.datatools.storage.model;

import java.util.*;

import org.joda.time.*;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.datatools.storage.SecurityProperties;

public class SecurityPropertiesEntity
	implements SecurityProperties
{
	private Long id;
	private SymbolEntity symbol;
	private String displayName;
	private DateTime expirationTime, startingTime;
	private Currency currencyOfCost;
	
	public SecurityPropertiesEntity() {
		super();
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public SymbolEntity getSymbol() {
		return symbol;
	}
	
	public void setSymbol(SymbolEntity id) {
		this.symbol = id;
	}
	
	// TODO: SymbolEntity needs refactoring
	@Override
	public Symbol getSymbolInfo() {
		return symbol.getSymbol();
	}
	
	@Override
	public String getDisplayName() {
		return displayName;
	}
	
	public void setDisplayName(String name) {
		this.displayName = name;
	}
	
	@Override
	public DateTime getExpirationTime() {
		return expirationTime;
	}
	
	public void setExpirationTime(DateTime time) {
		this.expirationTime = time;
	}
	
	@Override
	public Currency getCurrencyOfCost() {
		return currencyOfCost;
	}
	
	public void setCurrencyOfCost(Currency currency) {
		this.currencyOfCost = currency;
	}

	@Override
	public DateTime getStartingTime() {
		return startingTime;
	}
	
	public void setStartingTime(DateTime time) {
		this.startingTime = time;
	}

}

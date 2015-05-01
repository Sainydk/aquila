package ru.prolib.aquila.probe.storage.dao;

import java.util.List;

import ru.prolib.aquila.probe.storage.model.TradingSessionPropertiesEntity;

public interface TradingSessionPropertiesRepository {
	
	public TradingSessionPropertiesEntity createEntity();
	
	public TradingSessionPropertiesEntity getById(Long id);
	
	public List<TradingSessionPropertiesEntity> getAll();
	
	public void update(TradingSessionPropertiesEntity entity);
	
	public void delete(TradingSessionPropertiesEntity entity);

}

package ru.prolib.aquila.quik.dde;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Фасад подсистемы кэша DDE.
 */
public class Cache {
	private final OrdersCache orders;
	private final TradesCache trades;
	private final SecuritiesCache securities;
	private final PortfoliosFCache portfolios_F;
	private final PositionsFCache positions_F;
	private final StopOrdersCache stopOrders;
	private final PartiallyKnownObjects partiallyKnown;
	
	public Cache(PartiallyKnownObjects partiallyKnown,
			OrdersCache orders,
			TradesCache trades,
			SecuritiesCache securities,
			PortfoliosFCache portfolios_F,
			PositionsFCache positions_F,
			StopOrdersCache stopOrders)
	{
		super();
		this.partiallyKnown = partiallyKnown;
		this.orders = orders;
		this.trades = trades;
		this.securities = securities;
		this.portfolios_F = portfolios_F;
		this.positions_F = positions_F;
		this.stopOrders = stopOrders;
	}
	
	/**
	 * Получить кэш таблицы заявок.
	 * <p>
	 * @return кэш
	 */
	public synchronized OrdersCache getOrdersCache() {
		return orders;
	}
	
	/**
	 * Получить кэш таблицы собственных сделок.
	 * <p>
	 * @return кэш
	 */
	public synchronized TradesCache getTradesCache() {
		return trades;
	}
	
	/**
	 * Получить кэш таблицы инструментов.
	 * <p>
	 * @return кэш
	 */
	public synchronized SecuritiesCache getSecuritiesCache() {
		return securities;
	}
	
	/**
	 * Получить кэш таблицы портфелей ФОРТС.
	 * <p>
	 * @return кэш
	 */
	public synchronized PortfoliosFCache getPortfoliosFCache() {
		return portfolios_F;
	}
	
	/**
	 * Получить кэш таблицы позиций по деривативам.
	 * <p>
	 * @return кэш
	 */
	public synchronized PositionsFCache getPositionsFCache() {
		return positions_F;
	}
	
	/**
	 * Получить кэш таблицы стоп-заявок.
	 * <p>
	 * @return кэш
	 */
	public synchronized StopOrdersCache getStopOrdersCache() {
		return stopOrders;
	}
	
	/**
	 * Получить фасад доступа к объектам по неполным идентификационным данным.
	 * <p>
	 * @return фасад доступа
	 */
	public synchronized PartiallyKnownObjects getPartiallyKnownObjects() {
		return partiallyKnown;
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == null ) {
			return false;
		}
		if ( other == this ) {
			return true;
		}
		if ( other.getClass() != Cache.class ) {
			return false;
		}
		Cache o = (Cache) other;
		return new EqualsBuilder()
			.append(orders, o.orders)
			.append(trades, o.trades)
			.append(securities, o.securities)
			.append(portfolios_F, o.portfolios_F)
			.append(positions_F, o.positions_F)
			.append(stopOrders, o.stopOrders)
			.append(partiallyKnown, o.partiallyKnown)
			.isEquals();
	}

	/**
	 * Делегат к {@link PartiallyKnownObjects#registerAccount(Account)}.
	 * <p>
	 * @param account торговый счет
	 */
	public synchronized void registerAccount(Account account) {
		partiallyKnown.registerAccount(account);
	}
	
	/**
	 * Делегат к {@link PartiallyKnownObjects#registerSecurityDescriptor(SecurityDescriptor, String)}.
	 * <p>
	 * @param descr дескриптор инструмента
	 * @param shortName краткое наименование инструмента
	 */
	public synchronized void
		registerSecurityDescriptor(SecurityDescriptor descr, String shortName)
	{
		partiallyKnown.registerSecurityDescriptor(descr, shortName);
	}
	
	/**
	 * Делегат к {@link PartiallyKnownObjects#getAccount(String)}.
	 * <p>
	 * @param accountCode код торгового счета
	 * @return счет или null, если нет такого счета
	 */
	public synchronized Account getAccount(String accountCode) {
		return partiallyKnown.getAccount(accountCode);
	}
	
	/**
	 * Делегат к {@link PartiallyKnownObjects#getAccount(String, String)}.
	 * <p>
	 * @param clientCode код клиента
	 * @param accountCode код торгового счета
	 * @return счет или null, если нет такого счета
	 */
	public synchronized Account
		getAccount(String clientCode, String accountCode)
	{
		return partiallyKnown.getAccount(clientCode, accountCode);
	}
	
	/**
	 * Делегат к {@link PartiallyKnownObjects#getSecurityDescriptorByCodeAndClass(String, String)}.
	 * <p>
	 * @param code код инструмента
	 * @param classCode код класса инструмента
	 * @return дескриптор инструмента или null, если нет такого инструмента
	 */
	public synchronized SecurityDescriptor
		getSecurityDescriptorByCodeAndClass(String code, String classCode)
	{
		return partiallyKnown
			.getSecurityDescriptorByCodeAndClass(code, classCode);
	}
	
	/**
	 * Делегат к {@link PartiallyKnownObjects#getSecurityDescriptorByName(String)}.
	 * <p>
	 * @param name краткое наименование инструмента
	 * @return дескриптор инструмента или null, если нет такого инструмента
	 */
	public synchronized SecurityDescriptor
		getSecurityDescriptorByName(String name)
	{
		return partiallyKnown.getSecurityDescriptorByName(name);
	}
	
	/**
	 * Делегат к {@link PartiallyKnownObjects#isAccountRegistered(String)}.
	 * <p>
	 * @param accountCode код торгового счета
	 * @return наличие счета с таким кодом
	 */
	public synchronized boolean
		isAccountRegistered(String accountCode)
	{
		return partiallyKnown.isAccountRegistered(accountCode);
	}
	
	/**
	 * Делегат к {@link PartiallyKnownObjects#isAccountRegistered(String, String)}.
	 * <p>
	 * @param clientCode код клиента
	 * @param accountCode код торгового счета
	 * @return наличие счета с такими кодами
	 */
	public synchronized boolean
		isAccountRegistered(String clientCode, String accountCode)
	{
		return partiallyKnown.isAccountRegistered(clientCode, accountCode);
	}
	
	/**
	 * Делегат к {@link PartiallyKnownObjects#isSecurityDescriptorRegistered(String)}.
	 * <p>
	 * @param name краткое наименование инструмента
	 * @return наличие дескриптора с таким наименованием
	 */
	public synchronized boolean
		isSecurityDescriptorRegistered(String name)
	{
		return partiallyKnown.isSecurityDescriptorRegistered(name);
	}
	
	/**
	 * Делегат к {@link PartiallyKnownObjects#isSecurityDescriptorRegistered(String, String)}.
	 * <p>
	 * @param code код инструмента
	 * @param classCode код класса инструмента
	 * @return наличие дескриптора с такими кодами
	 */
	public synchronized boolean
		isSecurityDescriptorRegistered(String code, String classCode)
	{
		return partiallyKnown.isSecurityDescriptorRegistered(code, classCode);
	}
	
	/**
	 * Делегат к {@link SecuritiesCache#getAll()}.
	 * <p>
	 * @return список кэш-записей
	 */
	public synchronized List<SecurityCache> getAllSecurities() {
		return securities.getAll();
	}
	
	/**
	 * Делегат к {@link SecuritiesCache#OnCacheUpdate()}.
	 * <p>
	 * @return тип события
	 */
	public synchronized EventType OnSecuritiesCacheUpdate() {
		return securities.OnCacheUpdate();
	}
	
	/**
	 * Делегат к {@link PortfoliosFCache#getAll()}.
	 * <p>
	 * @return список кэш-записей
	 */
	public synchronized List<PortfolioFCache> getAllPortfoliosF() {
		return portfolios_F.getAll();
	}
	
	/**
	 * Делегат к {@link PortfoliosFCache#OnCacheUpdate()}.
	 * <p>
	 * @return тип события
	 */
	public synchronized EventType OnPortfoliosFCacheUpdate() {
		return portfolios_F.OnCacheUpdate();
	}
	
	/**
	 * Делегат к {@link PositionsFCache#getAll()}.
	 * <p>
	 * @return список кэш-записей
	 */
	public synchronized List<PositionFCache> getAllPositionsF() {
		return positions_F.getAll();
	}
	
	/**
	 * Делегат к {@link PositionsFCache#OnCacheUpdate()}.
	 * <p>
	 * @return тип события
	 */
	public synchronized EventType OnPositionsFCacheUpdate() {
		return positions_F.OnCacheUpdate();
	}
	
	/**
	 * Делегат к {@link OrdersCache#get(Long)}.
	 * <p>
	 * @param orderId номер заявки
	 * @return кэш-запись заявки или null, если нет соответствующей записи
	 */
	public synchronized OrderCache getOrderCache(long orderId) {
		return orders.get(orderId);
	}
	
	/**
	 * Делегат к {@link TradesCache#getAllByOrderId(Long)}.
	 * <p>
	 * @param orderId номер заявки
	 * @return список кэш-записей о сделках заявки
	 */
	public synchronized List<TradeCache> getAllTradesByOrderId(long orderId) {
		return trades.getAllByOrderId(orderId);
	}
	
	/**
	 * Делегат к {@link OrdersCache#getAll()}.
	 * <p>
	 * @return список кэш-записей
	 */
	public synchronized List<OrderCache> getAllOrders() {
		return orders.getAll();
	}
	
	/**
	 * Делегат к {@link TradesCache#OnCacheUpdate()}.
	 * <p>
	 * @return тип события
	 */
	public synchronized EventType OnTradesCacheUpdate() {
		return trades.OnCacheUpdate();
	}
	
	/**
	 * Делегат к {@link OrdersCache#OnCacheUpdate()}.
	 * <p>
	 * @return тип события
	 */
	public synchronized EventType OnOrdersCacheUpdate() {
		return orders.OnCacheUpdate();
	}

}

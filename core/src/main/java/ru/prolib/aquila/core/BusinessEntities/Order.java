package ru.prolib.aquila.core.BusinessEntities;

import java.util.List;
import org.joda.time.DateTime;
import ru.prolib.aquila.core.EventType;

/**
 * Базовый интерфейс заявки.
 * <p>
 * 2012-05-30<br>
 * $Id: Order.java 542 2013-02-23 04:15:34Z whirlwind $
 */
public interface Order {
	public static final int VERSION = 1;
	
	/**
	 * Получить тип события: в случае провала операции
	 * <p>
	 * @return тип события
	 */
	public EventType OnFailed();

	/**
	 * Получить тип события: заявка зарегистрирована на бирже.
	 * <p>
	 * @return тип события
	 */
	public EventType OnRegistered();
	
	/**
	 * Получить тип события: не удалось зарегистрировать заявку.
	 * <p>
	 * @return тип события
	 */
	public EventType OnRegisterFailed();

	/**
	 * Получить тип события: заявка отменена.
	 * <p>
	 * @return тип события
	 */
	public EventType OnCancelled();
	
	/**
	 * Получить тип события: не удалось отменить заявку.
	 * <p>
	 * @return тип события
	 */
	public EventType OnCancelFailed();
	
	/**
	 * Получить тип события: заявка полностью исполнена.
	 * <p>
	 * Событие генерируется один раз.
	 * <p>
	 * @return тип события
	 */
	public EventType OnFilled();
	
	/**
	 * Получить тип события: заявка частично исполнена.
	 * <p>
	 * Данное событие возникает только в случае отмены частично исполненной
	 * заявки. Событие генерируется один раз.
	 * <p>
	 * @return тип события
	 */
	public EventType OnPartiallyFilled();
	
	/**
	 * Получить тип события: заявка изменена.
	 * <p>
	 * Данное событие возникает каждый раз при изменении внутреннего состояния
	 * заявки. 
	 * <p>
	 * @return тип события
	 */
	public EventType OnChanged();
	
	/**
	 * Получить тип события: заявка завершена.
	 * <p>
	 * Данное событие возникает единственный раз, когда заявка переводится в
	 * один из финальных статусов: исполнена, частично исполнена, отменена
	 * или ошибка.
	 * <p>
	 * @return тип события
	 */
	public EventType OnDone();
	
	/**
	 * Получить тип события: новая сделка по заявке.
	 * <p>
	 * Генерируется событие класса {@link OrderTradeEvent}.
	 * Актуально только для рыночных и лимитных заявок.
	 * <p>
	 * @return тип события
	 */
	public EventType OnTrade();

	/**
	 * Получить биржевой идентификатор заявки.
	 * <p>
	 * @return биржевой идентификатор заявки или null, если не определен 
	 */
	public Integer getId();
	
	/**
	 * Получить направление заявки.
	 * <p>
	 * @return направление заявки
	 */
	public Direction getDirection();
	
	/**
	 * Получить тип заявки.
	 * <p>
	 * @return тип заявки
	 */
	public OrderType getType();
	
	/**
	 * Получить портфель, в рамках которого торгуется данная заявка.
	 * <p>
	 * @return портфель
	 */
	public Portfolio getPortfolio() throws PortfolioException;
	
	/**
	 * Получить торговый счет.
	 * <p>
	 * @return торговый счет
	 */
	public Account getAccount();
	
	/**
	 * Получить торгуемый инструмент.
	 * <p>
	 * @return инструмент
	 */
	public Security getSecurity() throws SecurityException;
	
	/**
	 * Получить дескриптор торгуемого инструмента.
	 * <p>
	 * @return дескриптор инструмента
	 */
	public SecurityDescriptor getSecurityDescriptor();
	
	/**
	 * Получить количество заявки.
	 * <p>
	 * @return количество заявки
	 */
	public Long getQty();
	
	/**
	 * Получить статус заявки.
	 * <p>
	 * @return статус заявки
	 */
	public OrderStatus getStatus();
	
	/**
	 * Получить неисполненный остаток заявки.
	 * <p> 
	 * @return несполненное количество
	 */
	public Long getQtyRest();
	
	/**
	 * Получить цену заявки.
	 * <p>
	 * @return цена заявки или null, если цена не предусмотрена
	 */
	public Double getPrice();
	
	/**
	 * Получить фактическую стоимость исполненной части заявки.
	 * <p>
	 * Фактическая стоимость совпадает с суммарным объемом по всем
	 * сделкам, относящимся к данной заявке. Фактическая стоимость выражается
	 * в валюте торгового счета. 
	 * <p>
	 * @return стоимость исполненной части
	 */
	public Double getExecutedVolume();
	
	/**
	 * Получить среднюю цену исполненной части заявки.
	 * <p>
	 * В отличии от {@link Order#getExecutedVolume()}, данный метод возвращает
	 * среднее значение цены, выраженной в единицах цены инструмента. Например,
	 * для фьючерса на индекс РТС это будет средняя цена в пунктах.
	 * <p>
	 * @return средняя цена исполненной части
	 */
	public Double getAvgExecutedPrice();
	
	/**
	 * Получить терминал заявки.
	 * <p>
	 * @return терминал
	 */
	public Terminal getTerminal();

	/**
	 * Получить время выставления заявки.
	 * <p>
	 * @return время выставления заявки или null, если заявка не выставлена
	 */
	public DateTime getTime();
	
	/**
	 * Получить время последнего изменения заявки.
	 * <p>
	 * Подразумевается время фиксации финального статуса заявки (снятие,
	 * сведение, частичное сведение).
	 * <p>
	 * @return время последнего изменения или null, если заявка не в финальном
	 * статусе
	 */
	public DateTime getLastChangeTime();
	
	/**
	 * Получить список сделок заявки.
	 * <p>
	 * Только для лимитных и рыночных заявок. Сделки отсортированы по номерам.
	 * <p>
	 * @return список сделок
	 */
	public List<Trade> getTrades();
	
	/**
	 * Проверить наличие сделки с указанным номером.
	 * <p>
	 * @param tradeId номер сделки
	 * @return true - указанная сделка есть в списке сделок заявки, false - нет
	 */
	public boolean hasTrade(long tradeId);
	
	/**
	 * Получить последнюю сделку.
	 * <p>
	 * @return последняя сделка или null, если нет сделок
	 */
	public Trade getLastTrade();
	
	/**
	 * Получить время последней сделки.
	 * <p>
	 * @return время последней сделки или null, если нет сделок
	 */
	public DateTime getLastTradeTime();
	
	/**
	 * Получить служебную информацию заявки.
	 * <p>
	 * @return служебная информация
	 */
	public OrderSystemInfo getSystemInfo();
	
	/**
	 * Получить контроллер активации заявки.
	 * <p>
	 * @return контроллер
	 */
	public OrderActivator getActivator();
	
	/**
	 * Получить комментарий заявки.
	 * <p>
	 * @return комментарий
	 */
	public String getComment();
	
}
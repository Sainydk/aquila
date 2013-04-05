package ru.prolib.aquila.core;

/**
 * Интерфейс типа события.
 * <p> 
 * 2012-04-09<br>
 * $Id: EventType.java 513 2013-02-11 01:17:18Z whirlwind $
 */
public interface EventType {

	/**
	 * Подписаться на событие.
	 * <p>
	 * @param listener получатель
	 */
	public void addListener(EventListener listener);

	/**
	 * Отписаться от события.
	 * <p>
	 * @param listener получатель
	 */
	public void removeListener(EventListener listener);
	
	/**
	 * Проверить наличие указанного наблюдателя.
	 * <p>
	 * @param listener получатель
	 * @return результат проверки: true - если указанный получатель в списке
	 * наблюдателей данного типа, false - получатель не является наблюдателем
	 * события данного типа.
	 */
	public boolean isListener(EventListener listener);
	
	/**
	 * Получить идентификатор типа события.
	 * <p>
	 * Идентификатор позволяет отличать конкретный тип события среди множества
	 * других типов по уникальной строке. Идентификатор задается явно при
	 * создании объекта или назначается автоматически, если не указан.
	 * Предназначен для использования в отладочных целях.
	 * <p>
	 * @return строковый идентификатор
	 */
	public String getId();
	
	/**
	 * Получить полный идентификатор в виде строки.
	 * <p>
	 * @return полный идентификатор
	 */
	public String asString();

}
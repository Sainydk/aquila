package ru.prolib.aquila.core.data;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Интерфейс последовательности значений.
 * <p>
 * @param <T> тип значения
 * <p>
 * 2012-04-17<br>
 * $Id: Series.java 565 2013-03-10 19:32:12Z whirlwind $
 */
public interface Series<T> {
	
	/**
	 * Идентификатор значения по-умолчанию.
	 */
	public static final String DEFAULT_ID = "default";

	/**
	 * Получить строковый идентификатор значения.
	 * <p>
	 * @return идентификатор
	 */
	public String getId();

	/**
	 * Получить текущее значение.
	 * <p>
	 * @return текущее значение
	 * @throws ValueNotExistsException нет ни одного значения в истории
	 */
	public T get() throws ValueException;

	/**
	 * Получить значение по индексу.
	 * <p>
	 * @param index Индекс значения на шкале времени. 0 - самое первое в
	 * хранимой истории значение. Отрицательные индексы используются для
	 * обращению к данным в прошлом относительно конца данных. -1 -
	 * предпоследнее значение, -2 - пред-предпоследнее значение, и т. д.
	 * @return значение
	 * @throws ValueOutOfDateException индекс указывает на архивное значение
	 * @throws ValueOutOfRangeException индекс за границами диапазона
	 * @throws ValueException
	 */
	public T get(int index) throws ValueException;

	/**
	 * Получить количество элементов ряда.
	 * <p>
	 * @return количество элементов
	 */
	public int getLength();
	
	/**
	 * Получить тип события: добавлено значение.
	 * <p>
	 * @return тип события
	 */
	public EventType OnAdd();
	
	/**
	 * Получить тип события: значение обновлено.
	 * <p>
	 * @return тип события
	 */
	public EventType OnUpd();

}
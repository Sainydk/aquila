package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.EventType;

/**
 * Интерфейс спецификации портфеля.
 * <p>
 * 2013-01-11<br>
 * $Id: SetupPortfolio.java 562 2013-03-06 15:22:54Z whirlwind $
 */
public interface SetupPortfolio extends SetupPositions {
	
	/**
	 * Проверить наличие изменений.
	 * <p>
	 * @return наличие изменений
	 */
	public boolean hasChanged();
	
	/**
	 * Отменить изменения.
	 * <p>
	 * Если изменений не было, то ничего не происходит. Если с момента
	 * создания или последней фиксации были изменения, то восстанавливает
	 * предыдущее состояние, затем генерируется событие {@link #OnRollback()}.
	 */
	public void rollback();
	
	/**
	 * Зафиксировать изменения.
	 * <p>
	 * Если изменений не было, то ничего не происходит. Если были изменения,
	 * то эти изменения становятся текущим состоянием, после чего генерируется
	 * событие типа {@link #OnCommit()}.
	 */
	public void commit();
	
	/**
	 * Форсировать событие об изменении.
	 * <p>
	 * Генерирует событие об изменении сетапа портфеля, даже если фактически
	 * изменения отсутствует.
	 */
	public void forceCommit();
	
	/**
	 * Получить тип события: при фиксации изменений.
	 * <p>
	 * При фиксации изменений генерируется событие типа
	 * {@link SetupPositionsEvent}, сопровождающееся набором зафиксированных
	 * изменений. То есть позиций, которые образовались после фиксации. 
	 * <p>
	 * @return тип события
	 */
	public EventType OnCommit();
	
	/**
	 * Получить тип события: при отмене изменений.
	 * <p>
	 * При отмене изменений генерируется событие типа
	 * {@link SetupPositionsEvent}, которое сопровождается набором начальных
	 * позиций. То есть набор позиций, который был актуален на момент до
	 * внесения изменений.
	 * <p>
	 * @return тип события
	 */
	public EventType OnRollback();
	
	/**
	 * Сбросить изменения если есть.
	 */
	public void resetChanges();

}

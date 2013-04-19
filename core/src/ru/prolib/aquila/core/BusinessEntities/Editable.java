package ru.prolib.aquila.core.BusinessEntities;

/**
 * Интерфейс редактируемого объекта.
 * <p>
 * 2012-09-03<br>
 * $Id: Editable.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public interface Editable {

	/**
	 * Проверить наличие изменений.
	 * <p>
	 * @return true - имеются изменения, false - изменений нет.
	 */
	public boolean hasChanged();
	
	/**
	 * Проверить наличие изменений по идентификатору операции.
	 * <p>
	 * @param changeId идентификатор операции
	 * @return true - имеются изменения, false - изменений нет.
	 */
	public boolean hasChanged(Integer changeId);

	/**
	 * Сбросить признак изменения.
	 * <p>
	 * Вызов этого метода сбрасывает признак наличия любых изменений. После
	 * этого, вызов {@link #hasChanged()} возвращает false до появления новых
	 * изменений.
	 */
	public void resetChanges();

	/**
	 * Установить признак доступности объекта.
	 * <p>
	 * Признак доступности позволяет поставщику данных различать объекты,
	 * созданные по запросу потребителя, но еще не проинициализированные
	 * поступившими данными и объекты, которым уже было задано первоначальное
	 * состояние.
	 * <p>
	 * @param flag признак доступности
	 */
	public void setAvailable(boolean flag);

	/**
	 * Проверить признак доступности объекта.
	 * <p>
	 * См. описание метода {@link #setAvailable(boolean)}.
	 * <p>
	 * @return признак доступности
	 */
	public boolean isAvailable();
	
	/**
	 * Генерировать событие об изменении атрибутов.
	 * <p>
	 * @throws EditableObjectException ошибка генерации события
	 */
	public void fireChangedEvent() throws EditableObjectException;
	
	/**
	 * Установить признак изменения объекта.
	 * <p>
	 * После вызова этого метода, метод {@link #hasChanged()} начинает
	 * возвращать true, до тех пор, пока признак изменения не будет
	 * сброшен вызовом метода {@link #resetChanges()}.
	 */
	public void setChanged();

}
/**
 * Сборка объектов бизнес модели.
 * <p>
 * Данный пакет содержит компоненты сборки и актуализации объектов бизнес модели
 * на основании данных, полученных через IB API. Процедуры сборки разделены на
 * две категории: моментальная сборка и позднее согласование.
 * <p>
 * Моментальная сборка используется в тех случаях, когда полученные данные могут
 * быть сопоставлены соответствующему объекту непосредственно в момент получения
 * этих данных. К таким данным относятся тиковые данные инструмента, атрибуты
 * портфеля - эти объекты не связаны с объектами других типов и не требуют
 * согласования.
 * <p>
 * Позднее согласование используется для объектов, тип которых зависит от
 * других типов объектов. Например, объект позиции не может быть обновлен пока
 * не был получен соответствующий портфель и инструмент. Объект заявки так же
 * не может быть собран, пока не доступны соответствующие заявки портфель и
 * инструмент. Кроме того, состояние заявки согласовывание на основании сделок,
 * которые так же представляют собой сущности отдельного типа.
 * <p>
 * Процедуры позднего согласования используют кэширование данных. При получении
 * данных, требующих позднего согласования, эти данные инкапсулируются в виде
 * записи и помещаются в кэш. Помещение данных в кэш автоматически генерирует
 * событие, которое отслеживается и обрабатывается соответствующим сборщиком.
 * Таким образом, простые процедуры сборки сосредоточены в обработчике
 * поступающих данных, а более сложные вынесены за пределы обработчика, что бы
 * избежать сложной логики на этапе приема данных.
 * <p>
 */
package ru.prolib.aquila.ib.assembler;
package ru.prolib.aquila.probe.timeline;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.threeten.extra.Interval;

/**
 * Последовательность событий.
 * <p>
 * Данный класс сортирует и группирует поступающие события в виде стеков.
 * <p>
 * Время POA (ТА, точка актуальности) указывает на текущее местоположение
 * симуляции на временной шкале. За начальное значение ТА берется время начала
 * рабочего периода. Попытка добавить устаревшее событие (датированное
 * временем меньшим чем текущая ТА) приведет к возбуждению исключения.
 * <p>
 * Момент достаточности данных определяется пользовательским кодом. Сдвиг точки
 * актуальности выполняется при извлечении очередного стека событий: ТА
 * сдвигается в будущее на миллисекунду (см. {@link #pullStack()}).
 */
public class TLEventQueue {
	private final Map<LocalDateTime, TLEventStack> stacks;
	private final LocalDateTime end;
	private LocalDateTime poa;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param interval рабочий период
	 */
	public TLEventQueue(Interval interval) {
		super();
		poa = LocalDateTime.ofInstant(interval.getStart(), ZoneOffset.UTC);
		end = LocalDateTime.ofInstant(interval.getEnd(), ZoneOffset.UTC);
		stacks = new Hashtable<LocalDateTime, TLEventStack>();
	}
	
	/**
	 * Получить значение точки актуальности.
	 * <p>
	 * @return точка актуальности
	 */
	public synchronized LocalDateTime getPOA() {
		return poa;
	}
	
	/**
	 * Получить активный рабочий период.
	 * <p>
	 * @return период
	 */
	public Interval getInterval() {
		return Interval.of(poa.toInstant(ZoneOffset.UTC), end.toInstant(ZoneOffset.UTC));
	}
	
	/**
	 * Добавить событие.
	 * <p>
	 * Размещает событие в общей последовательности.
	 * <p>
	 * @param event экземпляр события
	 * @throws TLOutOfIntervalException событие находится за пределами текущего
	 * интервала
	 */
	public synchronized void pushEvent(TLEvent event)
		throws TLOutOfIntervalException
	{
		LocalDateTime time = event.getTime();
		Interval currentInterval = getInterval();
		if ( ! currentInterval.contains(time.toInstant(ZoneOffset.UTC)) ) {
			throw new TLOutOfIntervalException(currentInterval, event);
		}
		TLEventStack stack = stacks.get(time);
		if ( stack == null ) {
			stack = new TLEventStack(event);
			stacks.put(time, stack);
		} else {
			stack.pushEvent(event);
		}
	}
	
	/**
	 * Получить текущий стек событий.
	 * <p>
	 * Текущий стек событий означает стек, соответствующей текущему значению ТА.
	 * Независимо от наличия стека на ТА, само значение ТА сдвигается на 1 мс.
	 * в будущее, что бы предотвратить в момент отработки стека добавление
	 * событий на момент времени, соответствующий извлеченному стеку.  
	 * <p>
	 * @return стек событий на ТА или null, если нет событий на ТА
	 */
	public synchronized TLEventStack pullStack() {
		TLEventStack stack = stacks.remove(poa);
		poa = poa.plus(1, ChronoUnit.MILLIS);
		return stack;
	}
	
	/**
	 * Переместить ТА на время следующего стека событий.
	 * <p>
	 * При определении конца данных ТА смещается на время кона периода.
	 * <p>
	 * @return true - если ТА смещено, false - если конец данных
	 */
	public synchronized boolean shiftToNextStack() {
		if ( stacks.size() == 0 ) {
			poa = end;
			return false;
		}
		List<LocalDateTime> dummy = new Vector<LocalDateTime>(stacks.keySet());
		Collections.sort(dummy);
		poa = dummy.get(0);
		return true;
	}
	
	/**
	 * Получить размер очереди.
	 * <p>
	 * @return количество стеков
	 */
	public synchronized int size() {
		return stacks.size();
	}
	
	/**
	 * Последовательность завершена?
	 * <p>
	 * Последовательность считается завершенной, когда ТА >= времени конца РП.
	 * При этом, не имеет значения есть ли в очереди события датированные
	 * за пределами РП.
	 * <p>
	 * @return true - последовательность завершена, false - последовательность
	 * еще не завершена
	 */
	public synchronized boolean finished() {
		return ! poa.isBefore(end);
	}
	
	/**
	 * Удалить все события.
	 */
	public synchronized void clear() {
		stacks.clear();
	}

}

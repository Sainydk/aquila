package ru.prolib.aquila.ta.SignalSource.PatternMatcher;

import ru.prolib.aquila.ta.Value;
import ru.prolib.aquila.ta.ValueException;
import ru.prolib.aquila.ta.indicator.Wz;

/**
 * Сигнал на продажу в зональной торговле по Теории Хаоса Билла Вильямса.
 * Сигнал формируется, если две последние зоны красные, а цена закрытия
 * последнего бара ниже цены закрытия предпоследнего.
 */
public class WilliamsZones_Sell extends PatternMatcher1Value {
	private final Value<Integer> wz;

	/**
	 * Конструктор.
	 * @param wz - индикатор зоны Вильямса
	 * @param src - значение цены закрытия
	 */
	public WilliamsZones_Sell(Value<Integer> wz, Value<Double> src) {
		super(src);
		this.wz = wz;
	}
	
	public Value<Integer> getWilliamsZones() {
		return wz;
	}

	@Override
	public boolean matches() throws ValueException {
		if ( wz.getLength() >= 2 ) {
			Integer za = wz.get(-1),
					zb = wz.get();
			Double  pa = src.get(-1),
					pb = src.get();
			if ( za != null && zb != null && pa != null && pb != null
				&& za == Wz.RED && za == zb
				&& pb < pa)
			{
				return true;
			}
		}
		return false;
	}

}
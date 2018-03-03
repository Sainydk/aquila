package ru.prolib.aquila.utils.experimental.chart.swing.layer;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;

/**
 * The data of active limit orders.
 */
public interface ALOData {
	
	/**
	 * Get total volume of buy orders.
	 * <p>
	 * @return total volume
	 */
	public CDecimal getTotalBuyVolume();
	
	/**
	 * Get total volume of sell orders.
	 * <p>
	 * @return total volume
	 */
	public CDecimal getTotalSellVolume();
	
	/**
	 * Get price level associated with the data.
	 * <p>
	 * @return the price
	 */
	public CDecimal getPrice();

}

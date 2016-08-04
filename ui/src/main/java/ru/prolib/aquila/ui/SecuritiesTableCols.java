package ru.prolib.aquila.ui;

import java.util.HashMap;
import java.util.Map;

import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.core.text.MsgID;
import ru.prolib.aquila.ui.msg.SecurityMsg;
import ru.prolib.aquila.ui.plugin.getters.*;
import ru.prolib.aquila.ui.wrapper.TableColumnAlreadyExistsException;
import ru.prolib.aquila.ui.wrapper.TableColumnWrp;
import ru.prolib.aquila.ui.wrapper.TableModel;

/**
 * $Id$
 */
public class SecuritiesTableCols {

	private final MsgID[] colIndex = {
		SecurityMsg.NAME,
		SecurityMsg.TYPE,
		SecurityMsg.CURRENCY,
		SecurityMsg.SYMBOL,
		SecurityMsg.EXCHANGE,
		SecurityMsg.LAST_PRICE,
		SecurityMsg.OPEN_PRICE,
		SecurityMsg.HIGH_PRICE,
		SecurityMsg.LOW_PRICE,
		SecurityMsg.CLOSE_PRICE,
		SecurityMsg.ASK_PRICE,
		SecurityMsg.ASK_SIZE,
		SecurityMsg.BID_PRICE,
		SecurityMsg.BID_SIZE,
		SecurityMsg.LOT_SIZE,
		SecurityMsg.TICK_SIZE,
		SecurityMsg.TICK_VALUE,
		SecurityMsg.SCALE,
		SecurityMsg.LOWER_PRICE,
		SecurityMsg.UPPER_PRICE,
		SecurityMsg.SETTLEMENT_PRICE,
		SecurityMsg.INITIAL_MARGIN
	};
	
	private static final Map<MsgID, Integer> width;
	private static final Map<MsgID, G<?>> getters;
	
	static {
		width = new HashMap<MsgID, Integer>();
		getters = new HashMap<MsgID, G<?>>();
		
		getters.put(SecurityMsg.NAME, new GSecurityName());
		getters.put(SecurityMsg.TYPE, new GSecurityType());
		getters.put(SecurityMsg.CURRENCY, new GSecurityCurrency());
		getters.put(SecurityMsg.SYMBOL, new GSecuritySymbol());
		getters.put(SecurityMsg.EXCHANGE, new GSecurityClass());
		getters.put(SecurityMsg.LAST_PRICE, new GSecurityLastPrice());
		getters.put(SecurityMsg.OPEN_PRICE, new GSecurityOpenPrice());
		getters.put(SecurityMsg.HIGH_PRICE, new GSecurityHighPrice());
		getters.put(SecurityMsg.LOW_PRICE, new GSecurityLowPrice());
		getters.put(SecurityMsg.CLOSE_PRICE, new GSecurityClosePrice());
		getters.put(SecurityMsg.ASK_PRICE, new GSecurityAskPrice());
		getters.put(SecurityMsg.ASK_SIZE, new GSecurityAskSize());
		getters.put(SecurityMsg.BID_PRICE, new GSecurityBidPrice());
		getters.put(SecurityMsg.BID_SIZE, new GSecurityBidSize());
		getters.put(SecurityMsg.LOT_SIZE, new GSecurityLotSize());
		getters.put(SecurityMsg.TICK_SIZE, new GSecurityMinStep());
		getters.put(SecurityMsg.TICK_VALUE, new GSecurityMinStepPrice());
		getters.put(SecurityMsg.SCALE, new GSecurityPrecision());
		getters.put(SecurityMsg.LOWER_PRICE, new GSecurityMinPrice());
		getters.put(SecurityMsg.UPPER_PRICE, new GSecurityMaxPrice());
		getters.put(SecurityMsg.SETTLEMENT_PRICE, new GSecuritySettlementPrice());
		getters.put(SecurityMsg.INITIAL_MARGIN, new GSecurityInitialMargin());
		
		width.put(SecurityMsg.NAME, 200);
	}
	
	public SecuritiesTableCols() {
		super();
	}
	
	public MsgID[] getColIndex() {
		return colIndex;
	}
	
	public Map<MsgID, Integer> getWidth() {
		return width;
	}
	
	public Map<MsgID, G<?>> getGetters() {
		return getters;
	}
	
	public void addColumnsToModel(TableModel model, IMessages messages)
			throws TableColumnAlreadyExistsException
	{
		for(int i = 0; i < colIndex.length; i++) {
			MsgID colId = colIndex[i];
			if(width.containsKey(colId)) {
				model.addColumn(new TableColumnWrp(colId, getters.get(colId),
					messages.get(colId), width.get(colId)));
			} else {
				model.addColumn(new TableColumnWrp(colId, getters.get(colId),
					messages.get(colId)));
			}
		}
	
	}
}

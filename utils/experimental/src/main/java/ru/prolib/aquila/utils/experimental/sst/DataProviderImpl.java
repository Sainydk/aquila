package ru.prolib.aquila.utils.experimental.sst;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.L1UpdatableStreamContainer;
import ru.prolib.aquila.core.BusinessEntities.MDUpdatableStreamContainer;
import ru.prolib.aquila.core.BusinessEntities.OrderException;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.DataProvider;
import ru.prolib.aquila.data.L1UpdateSource;
import ru.prolib.aquila.data.SymbolUpdateSource;

public class DataProviderImpl implements DataProvider {
	private final SymbolUpdateSource symbolUpdateSource;
	private final L1UpdateSource l1UpdateSource;
	
	public DataProviderImpl(SymbolUpdateSource symbolUpdateSource, L1UpdateSource l1UpdateSource) {
		this.symbolUpdateSource = symbolUpdateSource;
		this.l1UpdateSource = l1UpdateSource;
	}

	@Override
	public void subscribeStateUpdates(EditableSecurity security) {
		symbolUpdateSource.subscribeSymbol(security.getSymbol(), security);
	}

	@Override
	public void subscribeLevel1Data(Symbol symbol, L1UpdatableStreamContainer container) {
		l1UpdateSource.subscribeL1(symbol, container);
	}

	@Override
	public void subscribeLevel2Data(Symbol symbol, MDUpdatableStreamContainer container) {

	}

	@Override
	public void subscribeStateUpdates(EditablePortfolio portfolio) {

	}

	@Override
	public long getNextOrderID() {
		return 0;
	}

	@Override
	public void subscribeRemoteObjects(EditableTerminal terminal) {

	}

	@Override
	public void unsubscribeRemoteObjects(EditableTerminal terminal) {

	}

	@Override
	public void registerNewOrder(EditableOrder order) throws OrderException {

	}

	@Override
	public void cancelOrder(EditableOrder order) throws OrderException {

	}

}

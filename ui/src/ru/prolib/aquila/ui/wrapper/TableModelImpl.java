package ru.prolib.aquila.ui.wrapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.StarterException;
import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.data.row.RowAdapter;
/**
 * $Id: TableModel.java 578 2013-03-14 23:37:31Z huan.kaktus $
 */
public class TableModelImpl extends AbstractTableModel 
	implements TableModel 
{
	
	private static final long serialVersionUID = 5073295254324372206L;
	private static Logger logger = LoggerFactory.getLogger(TableModelImpl.class);
	/*
	 * Индекс названий колонок
	 */
	private List<String> index = new Vector<String>();
	/*
	 * колонки по названиям
	 */
	private Map<String, TableColumnWrp> cols = new HashMap<String, TableColumnWrp>();
	/*
	 * карта геттеров для RowAdapter
	 */
	private Map<String, G<?>> getters = new HashMap<String, G<?>>();
	/*
	 * геттер объекта строки из специфичного события
	 */
	private G<?> rowReader;
	/*
	 * список строк обернутых в адаптеры
	 */
	private List<RowAdapter> rows = new Vector<RowAdapter>(); 
	/*
	 * индекс строк
	 */
	private List<Object> rowIndex = new Vector<Object>();
	
	private DataSourceEventTranslator onRowAvailableListener;
	private DataSourceEventTranslator onRowChangedListener;
	
	
	public TableModelImpl(G<?> rowReader) {
		super();
		this.rowReader = rowReader;
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ui.wrapper.TableModel#addColumn(ru.prolib.aquila.ui.wrapper.TableColumn)
	 */
	@Override
	public synchronized void addColumn(TableColumnWrp column) throws TableColumnAlreadyExistsException {
		if(isColumnExists(column.getName())) {
			throw new TableColumnAlreadyExistsException(column.getName());
		}
		cols.put(column.getName(), column);
		getters.put(column.getName(), column.getGetter());
		index.add(column.getName());
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ui.wrapper.TableModel#isColumnExists(java.lang.String)
	 */
	@Override
	public synchronized boolean isColumnExists(String colId) {
		return cols.containsKey(colId);
	}
	
	public synchronized TableColumnWrp getColumn(String name) throws TableColumnNotExistsException {
		if(! isColumnExists(name)) {
			throw new TableColumnNotExistsException(name);
		}
		return cols.get(name);
	}
	
	public synchronized TableColumnWrp getColumn(int i)  throws TableColumnNotExistsException {
		try {
			return getColumn(index.get(i));
		} catch(ArrayIndexOutOfBoundsException e) {
			throw new TableColumnNotExistsException("Column not exists at index:"+i);
		}
	}
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ui.wrapper.TableModel#setOnRowAvailableListener(ru.prolib.aquila.ui.wrapper.DataSourceEventTranslator)
	 */
	@Override
	public void setOnRowAvailableListener(
				DataSourceEventTranslator onRowAvailableListener)
	{
		this.onRowAvailableListener = onRowAvailableListener;
	}
	
	public DataSourceEventTranslator getOnRowAvailableListener() {
		return onRowAvailableListener;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ui.wrapper.TableModel#setOnRowChangedListener(ru.prolib.aquila.ui.wrapper.DataSourceEventTranslator)
	 */
	@Override
	public void setOnRowChangedListener(
			DataSourceEventTranslator onRowChangedListener)
	{
		this.onRowChangedListener = onRowChangedListener;
	}
	
	public DataSourceEventTranslator getOnRowChangedListener() {
		return onRowChangedListener;
	}
	
	public G<?> getRowReader() {
		return rowReader;
	}
	
	public List<String> getIndex() {
		return index;
	}
	
	public Map<String, G<?>> getGetters() {
		return getters;
	}
	
	public void insertRow(Object source) {		
		RowAdapter row = new RowAdapter(source, getters);
		rows.add(row);
		rowIndex.add(source);
	}
	
	public List<RowAdapter> getRows() {
		return rows;
	}
	
	public List<Object> getRowIndex() {
		return rowIndex;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int col) {		
		return cols.get(index.get(col)).getText();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return index.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return rows.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int r, int c) {
		try {
			RowAdapter row = rows.get(r);
			return row.get(index.get(c));
		} catch (ArrayIndexOutOfBoundsException e) {
			logger.debug("Out of bound exception");
			return null;
		}		
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.EventListener#onEvent(ru.prolib.aquila.core.Event)
	 */
	@Override
	public void onEvent(Event event) {
		if(event.getType() == onRowAvailableListener.OnEventOccur()) {
			EventTranslatorEvent e = (EventTranslatorEvent) event;
			Object r = rowReader.get(e.getSource());
			RowAdapter row = new RowAdapter(r, getters);
			int index = rowIndex.size();
			rowIndex.add(r);
			rows.add(row);
			fireTableRowsInserted(index, index);
			
		} else if (event.getType() == onRowChangedListener.OnEventOccur()) {
			EventTranslatorEvent e = (EventTranslatorEvent) event;
			Object r = rowReader.get(e.getSource());
			int index = rowIndex.indexOf(r);
			fireTableRowsUpdated(index, index);
		}
		
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.Starter#start()
	 */
	@Override
	public void start() throws StarterException {
		onRowAvailableListener.OnEventOccur().addListener(this);
		onRowChangedListener.OnEventOccur().addListener(this);
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.Starter#stop()
	 */
	@Override
	public void stop() throws StarterException {
		onRowAvailableListener.OnEventOccur().removeListener(this);
		onRowChangedListener.OnEventOccur().removeListener(this);
	}
}
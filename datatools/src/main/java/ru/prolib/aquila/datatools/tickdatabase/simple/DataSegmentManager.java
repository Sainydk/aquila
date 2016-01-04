package ru.prolib.aquila.datatools.tickdatabase.simple;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.data.Aqiterator;

/**
 * Data segment manager interface.
 */
public interface DataSegmentManager {

	/**
	 * Open data segment for writing.
	 * <p> 
	 * @param symbol - associated symbol
	 * @param date - associated date
	 * @return data segment
	 * @throws IOException - error opening the data segment for writing
	 */
	public DataSegment openSegment(Symbol symbol, LocalDate date)
			throws IOException;
	
	/**
	 * Close the previously opened data segment.
	 * <p>
	 * @param segment - the tick data segment
	 * @throws IOException - error closing the data segment
	 */
	public void closeSegment(DataSegment segment) throws IOException;
	
	/**
	 * Open a tick data reader.
	 * <p> 
	 * @param symbol - associated symbol
	 * @param date - associated date
	 * @return data reader
	 * @throws IOException - error opening data segment for reading
	 */
	public Aqiterator<Tick> openReader(Symbol symbol, LocalDate date) throws IOException;
	
	/**
	 * Close the previously opened data reader. 
	 * <p>
	 * @param reader - data reader
	 * @throws IOException - error closing data segment
	 */
	public void closeReader(Aqiterator<Tick> reader) throws IOException;
	
	/**
	 * Test the data availability.
	 * <p>
	 * @param symbol - symbol info
	 * @return - true - if at least one data segment exists for the specified
	 * security 
	 * @throws IOException - error accessing storage
	 */
	public boolean isDataAvailable(Symbol symbol) throws IOException;
	
	/**
	 * Test the data segment availability.
	 * <p>
	 * @param symbol - symbol info
	 * @param date - date of the data segment
	 * @return true - if the data segment exists, false - if not exists
	 * @throws IOException - error accessing storage
	 */
	public boolean isDataAvailable(Symbol symbol, LocalDate date) throws IOException;
	
	/**
	 * Get date of the first available segment.
	 * <p>
	 * @param symbol - symbol info
	 * @return date of the first available segment
	 * @throws IOException - error accessing storage or data not available 
	 */
	public LocalDate getDateOfFirstSegment(Symbol symbol) throws IOException;
	
	/**
	 * Get the date of the next available segment.
	 * <p>
	 * @param symbol - symbol info
	 * @param date - date starting from
	 * @return date of the next available data segment or null if no more
	 * segments after the specified date
	 * @throws IOException - error accessing storage
	 */
	public LocalDate getDateOfNextSegment(Symbol symbol, LocalDate date) throws IOException;
	
	/**
	 * Get date of the last available segment.
	 * <p>
	 * @param symbol - symbol info
	 * @return date of the last available segment
	 * @throws IOException - error accessing storage or data not available
	 */
	public LocalDate getDateOfLastSegment(Symbol symbol) throws IOException;
	
	/**
	 * Get list of available segments.
	 * <p>
	 * @param symbol - symbol info
	 * @return list of dates
	 * @throws IOException - error accessing storage or data not available
	 */
	public List<LocalDate> getSegmentList(Symbol symbol) throws IOException;

}

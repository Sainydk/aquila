package ru.prolib.aquila.web.utils.finam.datasim;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;
import java.util.zip.GZIPInputStream;

import com.csvreader.CsvReader;

import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.BusinessEntities.L1UpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

/**
 * Read the data of a single CSV file stored in FINAM export format.
 * <p>
 * We need to know a price scale when create because FINAM
 * format does not contain such information.
 */
public class FinamCsvL1UpdateReader implements CloseableIterator<L1Update> {
	private static final ZoneId ZONE = ZoneId.of("Europe/Moscow");
	private static final String HDR_DATE = "<DATE>";
	private static final String HDR_TIME = "<TIME>";
	private static final String HDR_PRICE = "<LAST>";
	private static final String HDR_SIZE = "<VOL>";
	private static final DateTimeFormatter timeFmt, dateFmt;
	
	static {
		timeFmt = DateTimeFormatter.ofPattern("HHmmss");
		dateFmt = DateTimeFormatter.ofPattern("yyyyMMdd");
	}
	
	private final CsvReader csvReader;
	private final Symbol symbol;
	private final int priceScale;
	private boolean closed = false;
	private boolean firstRecord = true;
	private L1Update lastUpdate;
	
	public FinamCsvL1UpdateReader(Symbol symbol, CsvReader csvReader, int priceScale) {
		this.symbol = symbol;
		this.csvReader = csvReader;
		this.priceScale = priceScale;
	}
	
	public FinamCsvL1UpdateReader(Symbol symbol, InputStream inputStream, int priceScale) {
		this(symbol, new CsvReader(new InputStreamReader(inputStream)), priceScale);
	}
	
	public FinamCsvL1UpdateReader(Symbol symbol, File file, int priceScale) throws IOException {
		this(symbol, new GZIPInputStream(new BufferedInputStream(new FileInputStream(file))), priceScale);
	}
	
	public FinamCsvL1UpdateReader(Symbol symbol, String file, int priceScale) throws IOException {
		this(symbol, new File(file), priceScale);
	}

	@Override
	public void close() throws IOException {
		if ( ! closed ) {
			csvReader.close();
			closed = true;
		}
	}

	@Override
	public boolean next() throws IOException {
		lastUpdate = null;
		if ( firstRecord ) {
			firstRecord = false;
			if ( ! csvReader.readHeaders() ) {
				return false;
			}
		}
		if ( ! csvReader.readRecord() ) {
			return false;
		}
		L1UpdateBuilder builder = new L1UpdateBuilder(symbol).withTrade();
		LocalDate date = LocalDate.parse(csvReader.get(HDR_DATE), dateFmt);
		LocalTime time = LocalTime.parse(csvReader.get(HDR_TIME), timeFmt);
		builder.withTime(ZonedDateTime.of(date, time, ZONE).toInstant())
			// TODO: replace me on something more faster
			.withPrice(CDecimalBD.of(csvReader.get(HDR_PRICE)).withScale(priceScale))
			.withSize(Long.valueOf(csvReader.get(HDR_SIZE)));
		lastUpdate = builder.buildL1Update();
		return true;
	}

	@Override
	public L1Update item() throws IOException, NoSuchElementException {
		if ( closed ) {
			throw new IOException("The reader has already been closed");
		}
		if ( lastUpdate == null ) {
			throw new NoSuchElementException();
		}
		return lastUpdate;
	}

}

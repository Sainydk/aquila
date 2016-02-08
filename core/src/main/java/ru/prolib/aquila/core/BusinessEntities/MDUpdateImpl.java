package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class MDUpdateImpl implements MDUpdate, MDUpdateHeader {
	private final MDUpdateHeader header;
	private final List<MDUpdateRecord> records;

	public MDUpdateImpl(MDUpdateHeader header, List<MDUpdateRecord> records) {
		super();
		this.header = header;
		this.records = records;
	}
	
	public MDUpdateImpl(MDUpdateHeader header) {
		this(header, new Vector<MDUpdateRecord>());
	}

	@Override
	public MDUpdateHeader getHeader() {
		return header;
	}

	@Override
	public List<MDUpdateRecord> getRecords() {
		return Collections.unmodifiableList(records);
	}

	@Override
	public MDUpdateType getType() {
		return header.getType();
	}

	@Override
	public Instant getTime() {
		return header.getTime();
	}

	@Override
	public Symbol getSymbol() {
		return header.getSymbol();
	}
	
	public MDUpdateRecord addRecord(Tick tick, MDTransactionType transactionType) {
		MDUpdateRecord record = new MDUpdateRecordImpl(tick, transactionType);
		records.add(record);
		return record;
	}

}
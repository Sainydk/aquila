package ru.prolib.aquila.data.storage.file;

import java.io.File;
import java.io.IOException;

import ru.prolib.aquila.data.storage.L1UpdateWriter;
import ru.prolib.aquila.data.storage.L1UpdateWriterFactory;

public class SimpleCsvL1UpdateWriterFactory implements L1UpdateWriterFactory {
	private File file;
	
	public SimpleCsvL1UpdateWriterFactory(File file) {
		this.file = file;
	}
	
	public SimpleCsvL1UpdateWriterFactory() {
		this(null);
	}

	@Override
	public synchronized L1UpdateWriter createWriter() throws IOException {
		if ( file == null ) {
			throw new IllegalStateException("File not defined");
		}
		return new SimpleCsvL1UpdateWriter(file);
	}
	
	public synchronized File getFile() {
		return file;
	}
	
	public synchronized void setFile(File file) {
		this.file = file;
	}
	
}
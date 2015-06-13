package ru.prolib.aquila.datatools.finam;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.SchedulerLocal;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.core.BusinessEntities.utils.BasicTerminalBuilder;
import ru.prolib.aquila.core.data.Tick;
import ru.prolib.aquila.datatools.tickdatabase.TickWriter;
import ru.prolib.aquila.datatools.tickdatabase.util.SmartFlushTickWriter;

public class IOHelperTest {
	private static final File root;
	private static final SecurityDescriptor descr;
	
	static {
		root = new File("fixture", "finam-tests");
		descr = new SecurityDescriptor("Si-6.15","SPB","USD",SecurityType.FUT);
	}
	
	private IOHelper helper;
	private EditableTerminal terminal;
	private EditableSecurity security;
	private IMocksControl control;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		root.mkdirs();
		helper = new IOHelper(root);
		terminal = new BasicTerminalBuilder().buildTerminal();
		security = terminal.getEditableSecurity(descr);
		security.setPrecision(2);
		security.setMinStepSize(0.01d);
	}
	
	@After
	public void tearDown() throws Exception {
		FileUtils.deleteDirectory(root);
	}
	
	@Test
	public void testCtor1() {
		assertNotNull(helper.getScheduler());
		assertNotNull(helper.getFlushSetup());
		assertTrue(helper.getScheduler() instanceof SchedulerLocal);
	}
	
	@Test
	public void testCreateOutputStream_OverrideExisting() throws Exception {
		File file = new File(root, "xxx");
		
		OutputStream os = helper.createOutputStream(file, false);
		
		assertNotNull(os);
		os.write("Hello, World!".getBytes());
		os.close();
		String actual = FileUtils.readFileToString(file);
		assertEquals("Hello, World!", actual);
	}
	
	@Test
	public void testCreateOutputStream_AppendExisting() throws Exception {
		File file = new File(root, "yyy");
		FileUtils.writeStringToFile(file, "Last hope");
		
		OutputStream os = helper.createOutputStream(file, true);
		
		assertNotNull(os);
		os.write(" from the dark side".getBytes());
		os.close();
		String actual = FileUtils.readFileToString(file);
		assertEquals("Last hope from the dark side", actual);
	}
	
	@Test (expected=FileNotFoundException.class)
	public void testCreateOutputStream_ThrowsIfFileNotExists()
			throws Exception
	{
		File file = new File(new File(root, "zulu24"), "xxx");
		
		helper.createOutputStream(file, true);
	}
	
	@Test
	public void testCreateGzipOutputStream() throws Exception {
		File file = new File(root, "zzz.gz");
		OutputStream os = helper.createGzipOutputStream(file);
		assertNotNull(os);
		os.write("Help with vocabulary".getBytes());
		os.close();
		
		InputStream is = new GZIPInputStream(new FileInputStream(file));
		
		String actual = IOUtils.toString(is);
		is.close();
		assertEquals("Help with vocabulary", actual);
	}
	
	@Test (expected=FileNotFoundException.class)
	public void testCreateGzipOutputStream_ThrowsIfFileNotExists()
			throws Exception
	{
		File file = new File(new File(root, "charlie"), "xxx");
		
		helper.createGzipOutputStream(file);
	}
	
	@Test
	public void testCreateInputStream() throws Exception {
		File file = new File(root, "test.txt");
		FileUtils.writeStringToFile(file, "Alien encounters");
		
		InputStream is = helper.createInputStream(file);
		
		assertNotNull(is);
		String actual = IOUtils.toString(is);
		is.close();
		assertEquals("Alien encounters", actual);
	}
	
	@Test (expected=FileNotFoundException.class)
	public void testCreateInputStream_ThrowsIfFileNotExists() throws Exception {
		File file = new File(new File(root, "beta"), "foo.txt");
		helper.createInputStream(file);
	}

	@Test
	public void testCreateGzipInputStream() throws Exception {
		File file = new File(root, "test.txt.gz");
		OutputStream os = new GZIPOutputStream(new FileOutputStream(file));
		os.write("Look at the picture".getBytes());
		os.close();
		
		InputStream is = helper.createGzipInputStream(file);
		
		assertNotNull(is);
		String actual = IOUtils.toString(is);
		is.close();
		assertEquals("Look at the picture", actual);
	}
	
	@Test (expected=FileNotFoundException.class)
	public void testCreateGzipInputStream_ThrowsIfFileNotExists() throws Exception {
		File file = new File(new File(root, "gamma"), "bar.csv.gz");
		helper.createGzipInputStream(file);
	}
	
	@Test
	public void testGetFile() {
		File expected = new File(root, "Si%2D6%2E15-SPB-USD-FUT"
				+ File.separator + "2015" + File.separator + "10"
				+ File.separator + "Si%2D6%2E15-SPB-USD-FUT-20151013.xx");
		
		File actual = helper.getFile(descr, new LocalDate(2015, 10, 13), ".xx");
		
		assertNotNull(actual);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetRootDir() {
		File expected = new File(root, "Si%2D6%2E15-SPB-USD-FUT");
		
		File actual = helper.getRootDir(descr);
		
		assertNotNull(actual);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetLeve1Dir() {
		File expected = new File(root, "Si%2D6%2E15-SPB-USD-FUT"
				+ File.separator + "2015");
		
		File actual = helper.getLevel1Dir(descr, new LocalDate(2015, 10, 13));
		
		assertNotNull(actual);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetLevel2Dir() {
		File expected = new File(root, "Si%2D6%2E15-SPB-USD-FUT"
				+ File.separator + "2015" + File.separator + "10");
		
		File actual = helper.getLevel2Dir(descr, new LocalDate(2015, 10, 13));
		
		assertNotNull(actual);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCopyStream() throws Exception {
		String expected = "Matching engine for startup stock";
		File src = new File(root, "input.txt");
		File dst = new File(root, "output.txt");
		FileUtils.writeStringToFile(src, expected);
		InputStream input = new FileInputStream(src);
		OutputStream output = new FileOutputStream(dst);
		
		helper.copyStream(input, output);

		input.close();
		output.close();
		input = new FileInputStream(dst);
		String actual = IOUtils.toString(input);
		input.close();
		assertNotNull(actual);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCreateCsvWriter() throws Exception {
		File file = new File(root, "data.csv");
		OutputStream output = new FileOutputStream(file);
		
		CsvTickWriter writer = helper.createCsvTickWriter(security, output);
		
		assertNotNull(writer);
		writer.writeHeader();
		writer.write(new Tick(new DateTime(2014, 1, 1, 20, 30, 55, 30), 2d, 5));
		writer.close();
		List<String> expected = new Vector<String>();
		expected.add("<TIME>,<LAST>,<VOL>,<MILLISECONDS>");
		expected.add("203055,2.00,5,30");
		InputStream input = new FileInputStream(file);
		List<String> actual = IOUtils.readLines(input, Charset.defaultCharset());
		input.close();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testAddSmartFlush() throws Exception {
		TickWriter writer = control.createMock(TickWriter.class);
		
		TickWriter dummy = helper.addSmartFlush(writer, "zulu24");
		
		assertNotNull(dummy);
		SmartFlushTickWriter flusher = (SmartFlushTickWriter) dummy;
		assertSame(helper.getScheduler(), flusher.getScheduler());
		assertSame(helper.getFlushSetup(), flusher.getSetup());
		assertSame(writer, flusher.getTickWriter());
	}

}
package ru.prolib.aquila.web.utils.moex;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.FDecimal;
import ru.prolib.aquila.core.BusinessEntities.FMoney;
import ru.prolib.aquila.data.DataFormatException;

public class MoexContractPtmlConverterTest {
	private MoexContractPtmlConverter converter;

	@Before
	public void setUp() throws Exception {
		converter = new MoexContractPtmlConverter();
	}

	@Test
	public void testToString() throws Exception {
		Map<Integer, Object> fix = new HashMap<>();
		fix.put(MoexContractField.CONTRACT_DESCR, "foobar");
		fix.put(MoexContractField.DELIVERY, LocalDate.of(1997, 8, 16));
		fix.put(MoexContractField.EXERCISE_FEE, 5.15d);
		fix.put(MoexContractField.FEE, 3.19d);
		fix.put(MoexContractField.FIRST_TRADING_DAY, LocalDate.of(2008, 10, 5));
		fix.put(MoexContractField.FX_EVENING_CLEARING, LocalTime.of(18, 45));
		fix.put(MoexContractField.FX_INTRADAY_CLEARING, LocalTime.of(13, 50));
		fix.put(MoexContractField.INITIAL_MARGIN, 12450.0d);
		fix.put(MoexContractField.INITIAL_MARGIN_DATE, LocalDate.of(2016, 8, 8));
		fix.put(MoexContractField.INTRADAY_FEE, 15.32d);
		fix.put(MoexContractField.LAST_TRADING_DAY, LocalDate.of(2045,  12, 10));
		fix.put(MoexContractField.LOT_SIZE, 10);
		fix.put(MoexContractField.LOWER_PRICE_LIMIT, 95.14d);
		fix.put(MoexContractField.NEGOTIATION_FEE, 1.02d);
		fix.put(MoexContractField.QUOTATION, "POINTS");
		fix.put(MoexContractField.SETTLEMENT, "DELIVERABLE");
		fix.put(MoexContractField.SETTLEMENT_PRICE, 1248.05d);
		fix.put(MoexContractField.SETTLEMENT_PROC_DESCR, "bla bla bla");
		fix.put(MoexContractField.SYMBOL, "MSFT");
		fix.put(MoexContractField.SYMBOL_CODE, "MS01");
		fix.put(MoexContractField.TICK_SIZE, new FDecimal("0.01"));
		fix.put(MoexContractField.TICK_VALUE, new FMoney("2.13", "RUB"));
		fix.put(MoexContractField.TYPE, "FUTURES");
		fix.put(MoexContractField.UPPER_PRICE_LIMIT, 120.83d);
		
		Map<Integer, String> actual = new HashMap<>();
		Iterator<Map.Entry<Integer, Object>> it = fix.entrySet().iterator();
		while ( it.hasNext() ) {
			Map.Entry<Integer, Object> dummy = it.next();
			int token = dummy.getKey();
			actual.put(token, converter.toString(token, dummy.getValue()));
		}
		
		Map<Integer, String> expected = new HashMap<>();
		expected.put(MoexContractField.CONTRACT_DESCR, "foobar");
		expected.put(MoexContractField.DELIVERY, "1997-08-16");
		expected.put(MoexContractField.EXERCISE_FEE, "5.15");
		expected.put(MoexContractField.FEE, "3.19");
		expected.put(MoexContractField.FIRST_TRADING_DAY, "2008-10-05");
		expected.put(MoexContractField.FX_EVENING_CLEARING, "18:45");
		expected.put(MoexContractField.FX_INTRADAY_CLEARING, "13:50");
		expected.put(MoexContractField.INITIAL_MARGIN, "12450.0");
		expected.put(MoexContractField.INITIAL_MARGIN_DATE, "2016-08-08");
		expected.put(MoexContractField.INTRADAY_FEE, "15.32");
		expected.put(MoexContractField.LAST_TRADING_DAY, "2045-12-10");
		expected.put(MoexContractField.LOT_SIZE, "10");
		expected.put(MoexContractField.LOWER_PRICE_LIMIT, "95.14");
		expected.put(MoexContractField.NEGOTIATION_FEE, "1.02");
		expected.put(MoexContractField.QUOTATION, "POINTS");
		expected.put(MoexContractField.SETTLEMENT, "DELIVERABLE");
		expected.put(MoexContractField.SETTLEMENT_PRICE, "1248.05");
		expected.put(MoexContractField.SETTLEMENT_PROC_DESCR, "bla bla bla");
		expected.put(MoexContractField.SYMBOL, "MSFT");
		expected.put(MoexContractField.SYMBOL_CODE, "MS01");
		expected.put(MoexContractField.TICK_SIZE, "0.01");
		expected.put(MoexContractField.TICK_VALUE, "2.13");
		expected.put(MoexContractField.TYPE, "FUTURES");
		expected.put(MoexContractField.UPPER_PRICE_LIMIT, "120.83");
		assertEquals(expected, actual);
	}
	
	@Test
	public void testToObject() throws Exception {
		Map<Integer, String> fix = new HashMap<>();
		fix.put(MoexContractField.CONTRACT_DESCR, "foobar");
		fix.put(MoexContractField.DELIVERY, "1997-08-16");
		fix.put(MoexContractField.EXERCISE_FEE, "5.15");
		fix.put(MoexContractField.FEE, "3.19");
		fix.put(MoexContractField.FIRST_TRADING_DAY, "2008-10-05");
		fix.put(MoexContractField.FX_EVENING_CLEARING, "18:45");
		fix.put(MoexContractField.FX_INTRADAY_CLEARING, "13:50");
		fix.put(MoexContractField.INITIAL_MARGIN, "12450.0");
		fix.put(MoexContractField.INITIAL_MARGIN_DATE, "2016-08-08");
		fix.put(MoexContractField.INTRADAY_FEE, "15.32");
		fix.put(MoexContractField.LAST_TRADING_DAY, "2045-12-10");
		fix.put(MoexContractField.LOT_SIZE, "10");
		fix.put(MoexContractField.LOWER_PRICE_LIMIT, "95.14");
		fix.put(MoexContractField.NEGOTIATION_FEE, "1.02");
		fix.put(MoexContractField.QUOTATION, "POINTS");
		fix.put(MoexContractField.SETTLEMENT, "DELIVERABLE");
		fix.put(MoexContractField.SETTLEMENT_PRICE, "1248.05");
		fix.put(MoexContractField.SETTLEMENT_PROC_DESCR, "bla bla bla");
		fix.put(MoexContractField.SYMBOL, "MSFT");
		fix.put(MoexContractField.SYMBOL_CODE, "MS01");
		fix.put(MoexContractField.TICK_SIZE, "0.01");
		fix.put(MoexContractField.TICK_VALUE, "2.13");
		fix.put(MoexContractField.TYPE, "FUTURES");
		fix.put(MoexContractField.UPPER_PRICE_LIMIT, "120.83");

		Map<Integer, Object> actual = new HashMap<>();
		Iterator<Map.Entry<Integer, String>> it = fix.entrySet().iterator();
		while ( it.hasNext() ) {
			Map.Entry<Integer, String> dummy = it.next();
			int token = dummy.getKey();
			actual.put(token, converter.toObject(token, dummy.getValue()));
		}

		Map<Integer, Object> expected = new HashMap<>();
		expected.put(MoexContractField.CONTRACT_DESCR, "foobar");
		expected.put(MoexContractField.DELIVERY, LocalDate.of(1997, 8, 16));
		expected.put(MoexContractField.EXERCISE_FEE, new FMoney("5.15", "RUB"));
		expected.put(MoexContractField.FEE, new FMoney("3.19", "RUB"));
		expected.put(MoexContractField.FIRST_TRADING_DAY, LocalDate.of(2008, 10, 5));
		expected.put(MoexContractField.FX_EVENING_CLEARING, LocalTime.of(18, 45));
		expected.put(MoexContractField.FX_INTRADAY_CLEARING, LocalTime.of(13, 50));
		expected.put(MoexContractField.INITIAL_MARGIN, new FMoney("12450", "RUB"));
		expected.put(MoexContractField.INITIAL_MARGIN_DATE, LocalDate.of(2016, 8, 8));
		expected.put(MoexContractField.INTRADAY_FEE, new FMoney("15.32", "RUB"));
		expected.put(MoexContractField.LAST_TRADING_DAY, LocalDate.of(2045,  12, 10));
		expected.put(MoexContractField.LOT_SIZE, 10);
		expected.put(MoexContractField.LOWER_PRICE_LIMIT, new FDecimal("95.14"));
		expected.put(MoexContractField.NEGOTIATION_FEE, new FMoney("1.02", "RUB"));
		expected.put(MoexContractField.QUOTATION, "POINTS");
		expected.put(MoexContractField.SETTLEMENT, "DELIVERABLE");
		expected.put(MoexContractField.SETTLEMENT_PRICE, new FDecimal("1248.05"));
		expected.put(MoexContractField.SETTLEMENT_PROC_DESCR, "bla bla bla");
		expected.put(MoexContractField.SYMBOL, "MSFT");
		expected.put(MoexContractField.SYMBOL_CODE, "MS01");
		expected.put(MoexContractField.TICK_SIZE, new FDecimal("0.01"));
		expected.put(MoexContractField.TICK_VALUE, new FMoney("2.13", "RUB"));
		expected.put(MoexContractField.TYPE, "FUTURES");
		expected.put(MoexContractField.UPPER_PRICE_LIMIT, new FDecimal("120.83"));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testToObject_TickSizeAndValue_ScientificNotation() throws Exception {
		assertEquals(new FDecimal("0.0001"), converter.toObject(MoexContractField.TICK_SIZE, "1.0E-4"));
		assertEquals(new FMoney("0.015", "RUB"), converter.toObject(MoexContractField.TICK_VALUE, "15.0E-3"));
	}
	
	@Test
	public void testToObject_TickSizeAndValue_TrailingZeroes() throws Exception {
		assertEquals(new FDecimal("0.01"), converter.toObject(MoexContractField.TICK_SIZE, "0.01000"));
		assertEquals(new FMoney("0.15", "RUB"), converter.toObject(MoexContractField.TICK_VALUE, "0.1500"));
	}

	@Test
	public void testToObject_TickSizeAndValue_NoDecimals() throws Exception {
		assertEquals(new FDecimal("10"), converter.toObject(MoexContractField.TICK_SIZE, "10.0"));
		assertEquals(new FMoney("1", "RUB"), converter.toObject(MoexContractField.TICK_VALUE, "1.0000"));
	}

	@Test (expected=DataFormatException.class)
	public void testToObject_TickSize_FormatError() throws Exception {
		converter.toObject(MoexContractField.TICK_SIZE, "foo");
	}

	@Test (expected=DataFormatException.class)
	public void testToObject_TickValue_FormatError() throws Exception {
		converter.toObject(MoexContractField.TICK_VALUE, "bar");
	}

}

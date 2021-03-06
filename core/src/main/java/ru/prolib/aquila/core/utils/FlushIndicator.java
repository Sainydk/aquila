package ru.prolib.aquila.core.utils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public interface FlushIndicator {
	void start();
	void waitForFlushing(long duration, TimeUnit unit)
			throws InterruptedException, TimeoutException;
}

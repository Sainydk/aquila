package ru.prolib.aquila.web.utils;

import java.io.File;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;
import com.machinepublishers.jbrowserdriver.Timezone;

public class WebDriverFactory {
	private static final int DEFAULT_SOCKET_TIMEOUT = 120000;
	
	public static WebDriver createJBrowserDriver(int socketTimeoutMs) {
		return new JBrowserDriver(Settings.builder()
				.timezone(Timezone.EUROPE_MOSCOW)
				.ssl("compatible")
				.blockAds(true)
				.headless(true)
				.quickRender(true)
				.socketTimeout(socketTimeoutMs)
				.build());
	}
	
	public static WebDriver createJBrowserDriver() {
		return createJBrowserDriver(DEFAULT_SOCKET_TIMEOUT);
	}
	
	public static WebDriver createFirefoxDriver() {
		FirefoxProfile profile = new FirefoxProfile();
    	File ffBinary = new File("D:/Program Files (x86)/Mozilla Firefox/firefox.exe");
    	return new FirefoxDriver(new FirefoxBinary(ffBinary), profile);
	}

}

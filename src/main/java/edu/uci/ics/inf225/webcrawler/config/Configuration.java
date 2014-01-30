package edu.uci.ics.inf225.webcrawler.config;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configuration {

	private static final String CONFIG_PATH = "cfg/webcrawler.properties";

	private static final Logger log = LoggerFactory.getLogger(Configuration.class);

	private static Properties properties;

	static {
		properties = new Properties();
		try {
			properties.load(new FileReader(CONFIG_PATH));
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	public static String get(String key, String defaultValue) {
		return Configuration.properties.getProperty(key, defaultValue);
	}

	public static String get(String key) {
		return Configuration.get(key, null);
	}
}

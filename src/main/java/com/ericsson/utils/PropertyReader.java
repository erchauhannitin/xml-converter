package com.ericsson.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PropertyReader {

	private static final Logger log = LoggerFactory.getLogger(PropertyReader.class);
	
	private static Properties configProperty = new Properties();
	private static String filePath = "resources/config.properties";
	private PropertyReader() {
	}

	public static String getPropertyValue(String propertyName) {

		try (InputStream configInput = new FileInputStream(filePath)) {
			if (configProperty.isEmpty()) {
				configProperty.load(configInput);
				configProperty.list(System.out);
			}
		} 
		catch (IOException ex) {
			log.error("Error Reading from Property file");
		}
		
		return configProperty.getProperty(propertyName);

	}
	
}

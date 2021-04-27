/**
 * 
 */
package com.tiempometa.macsha.one4all;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * @author gtasi
 *
 */
public class SettingsHandler {

	public static final String ONE_4_SIMULATOR_PROPERTIES = "/one4all_simulator.properties";
	public static final String ONE_4_SIMULATOR_LOG_FILES = "/one4all_log_files";
	private Logger logger = Logger.getLogger(SettingsHandler.class);
	private Properties properties = new Properties();
	private static final String propertiesPath = System.getProperty("user.home") + "/.tiempometa";

	public String getSetting(String setting) {
		return properties.getProperty(setting);
	}

	public String getSetting(String setting, String defaultValue) {
		return properties.getProperty(setting, defaultValue);
	}

	public void init() throws IOException {
		File propertiesPathFile = new File(propertiesPath);
		if (!propertiesPathFile.exists()) {
			propertiesPathFile.mkdir();
		}
		String propertiesFilename = propertiesPath + ONE_4_SIMULATOR_PROPERTIES;
		File propertiesFile = new File(propertiesFilename);
		if (!propertiesFile.exists()) {
			propertiesFile.createNewFile();
		}
		File logDirectory = new File(propertiesPath + ONE_4_SIMULATOR_LOG_FILES);
		if (!logDirectory.exists()) {
			logDirectory.mkdir();
		}
		FileReader propertiesReader = new FileReader(propertiesFile);
		properties.load(propertiesReader);
		propertiesReader.close();
		logger.debug("Loaded property names " + properties.stringPropertyNames());
	}

	public void setSetting(String setting, String value) {
		if (value == null) {
			properties.setProperty(setting, "");
		} else {
			properties.setProperty(setting, value);
		}
	}

	public void flush() throws IOException {
		String propertiesFilename = propertiesPath + ONE_4_SIMULATOR_PROPERTIES;
		FileWriter writer = new FileWriter(new File(propertiesFilename));
		properties.store(writer, "----");
		writer.close();
	}

	public static String getDefaultBackupPath() {
		return propertiesPath + ONE_4_SIMULATOR_LOG_FILES;
	}

}

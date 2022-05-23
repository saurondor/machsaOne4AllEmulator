/*
 * Copyright (c) 2019 Gerardo Esteban Tasistro Giubetic
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 */
package com.tiempometa.macsha.one4all;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 *
 */
public class SettingsHandler {

	public static final String ONE_4_SIMULATOR_PROPERTIES = "/one4all_simulator.properties";
	public static final String ONE_4_SIMULATOR_LOG_FILES = "/one4all_log_files";
	private Logger logger = LogManager.getLogger(SettingsHandler.class);
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

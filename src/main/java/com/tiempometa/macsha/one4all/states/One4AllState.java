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
package com.tiempometa.macsha.one4all.states;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tiempometa.macsha.one4all.commands.ClearFilesCommand;
import com.tiempometa.macsha.one4all.commands.GetFileCommand;
import com.tiempometa.macsha.one4all.commands.GetFileInfoCommand;
import com.tiempometa.macsha.one4all.commands.GetPassingsCommand;
import com.tiempometa.macsha.one4all.commands.GetProtocolCommand;
import com.tiempometa.macsha.one4all.commands.GetStatusCommand;
import com.tiempometa.macsha.one4all.commands.GetTimeCommand;
import com.tiempometa.macsha.one4all.commands.ListFilesCommand;
import com.tiempometa.macsha.one4all.commands.MacshaCommand;
import com.tiempometa.macsha.one4all.commands.NewFileCommand;
import com.tiempometa.macsha.one4all.commands.PasingsCommand;
import com.tiempometa.macsha.one4all.commands.PingCommand;
import com.tiempometa.macsha.one4all.commands.PushTagsCommand;
import com.tiempometa.macsha.one4all.commands.ReadBatteryCommand;
import com.tiempometa.macsha.one4all.commands.SetBounceCommand;
import com.tiempometa.macsha.one4all.commands.SetBuzzerCommand;
import com.tiempometa.macsha.one4all.commands.SetProtocolCommand;
import com.tiempometa.macsha.one4all.commands.SetTimeCommand;
import com.tiempometa.macsha.one4all.commands.StartCommand;
import com.tiempometa.macsha.one4all.commands.StopCommand;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 *
 */
public class One4AllState {

	private static final Logger logger = LogManager.getLogger(One4AllState.class);

	private long dateTimeOffset = 0;
	private float voltage = 24f;
	private float batteryLevel = 100f;
	private File backupPath = null;
	private File backupFile = null;
	private File logFile = null;
	private String backupFileName = null;
	private File[] backupFiles = null;
	private boolean hasPower = false;
	private boolean readingTags = false;
	private boolean pushingTags = false;
	private boolean buzzerOn = true;
	private String protocol = "3.0";
	private int bounce = 60;
	private UserInterfaceListener uiListener;
	private boolean antenna1 = false;
	private boolean antenna2 = false;
	private boolean antenna3 = false;
	private boolean antenna4 = false;
	private boolean antenna5 = false;
	private boolean antenna6 = false;
	private boolean antenna7 = false;
	private boolean antenna8 = false;

	public MacshaCommand handleCommand(MacshaCommand command) {
		if (command instanceof ClearFilesCommand) {
		}
		if (command instanceof GetFileCommand) {
			logger.info("Got GETFILE command");
			GetFileCommand getFileCommand = (GetFileCommand) command;
			updateUI();
			return command;
		}
		if (command instanceof GetFileInfoCommand) {

		}
		if (command instanceof GetPassingsCommand) {

		}
		if (command instanceof GetProtocolCommand) {

		}
		if (command instanceof GetTimeCommand) {

		}
		if (command instanceof ListFilesCommand) {
			logger.info("Got LISTFILES command");
			ListFilesCommand listFilesCommand = (ListFilesCommand) command;
			String[] files = new String[0];
			if (backupFiles != null) {
				files = new String[backupFiles.length];
				for (int i = 0; i < backupFiles.length; i++) {
					files[i] = backupFiles[i].getName();
				}
			}
			listFilesCommand.setFiles(files);
			updateUI();
			return command;

		}
		if (command instanceof NewFileCommand) {
			NewFileCommand newFileCommand = (NewFileCommand) command;
			try {
				createNewLogFile();
				newFileCommand.setFileName(getBackupFile());
			} catch (IOException e) {
				logger.error("Unable to create log file " + e.getMessage());
				newFileCommand.setErrorCode(MacshaCommand.RESPONSE_ERRFILE);
			} catch (Exception e) {
				logger.error("Backup file creation error " + e.getMessage());
				newFileCommand.setErrorCode(MacshaCommand.RESPONSE_ERR);
			}
			return command;
		}
		if (command instanceof PasingsCommand) {

		}
		if (command instanceof PingCommand) {
			logger.info("Got PING command");
			return command;
		}
		if (command instanceof PushTagsCommand) {
			logger.info("Got PUSHTAGS command");
			PushTagsCommand pushTagsCommand = (PushTagsCommand) command;
			pushingTags = pushTagsCommand.isPushStatus();
			updateUI();
			return command;
		}
		if (command instanceof ReadBatteryCommand) {
			logger.info("Got READBATTERY command");
			ReadBatteryCommand batteryCommand = (ReadBatteryCommand) command;
			batteryCommand.setCharge(batteryLevel);
			batteryCommand.setVoltage(voltage);
			batteryCommand.setHasPower(hasPower);
			return command;
		}
		if (command instanceof SetBounceCommand) {
			logger.info("Got SETBOUNCE command");
			SetBounceCommand bounceCommand = (SetBounceCommand) command;
			bounce = bounceCommand.getBounce();
			updateUI();
			return command;
		}
		if (command instanceof SetBuzzerCommand) {
			logger.info("Got SETBUZZER command");
			SetBuzzerCommand buzzerCommand = (SetBuzzerCommand) command;
			buzzerOn = buzzerCommand.isBuzzerStatus();
			updateUI();
			return command;
		}
		if (command instanceof SetProtocolCommand) {

		}
		if (command instanceof SetTimeCommand) {
			logger.info("Got SETTIME command");
			SetTimeCommand setTimeCommand = (SetTimeCommand) command;
			Date readerTime = setTimeCommand.getDateTime();
			Date systemTime = new Date();
			dateTimeOffset = readerTime.getTime() - systemTime.getTime();
			updateUI();
			return command;
		}
		if (command instanceof StartCommand) {
			logger.info("Got START command");
			StartCommand startCommand = (StartCommand) command;
			if (readingTags == false) {
				try {
					createNewLogFile();
					readingTags = true;
					startCommand.setLogFileName(backupFile.getName());
					updateUI();
				} catch (IOException e) {
					startCommand.setErrorCode(MacshaCommand.RESPONSE_ERRSTART);
				}
			}
			return command;
		}
		if (command instanceof StopCommand) {
			logger.info("Got STOP command");
			readingTags = false;
			updateUI();
			return command;
		}
		if (command instanceof GetStatusCommand) {
			GetStatusCommand getStatusCommand = (GetStatusCommand) command;
			getStatusCommand.setBatteryLevel(batteryLevel);
			getStatusCommand.setBatteryVoltage(voltage);
			getStatusCommand.setAntenna1(antenna1);
			getStatusCommand.setAntenna2(antenna2);
			getStatusCommand.setAntenna3(antenna3);
			getStatusCommand.setAntenna4(antenna4);
			getStatusCommand.setAntenna5(antenna5);
			getStatusCommand.setAntenna6(antenna6);
			getStatusCommand.setAntenna7(antenna7);
			getStatusCommand.setAntenna8(antenna8);
			getStatusCommand.setHasAcPower(hasPower);
			return command;
		}
		return null;
	}

	private void updateUI() {
		uiListener.updateReaderState();
	}

	public long getDateTimeOffset() {
		return dateTimeOffset;
	}

	public float getVoltage() {
		synchronized (this) {
			return voltage;
		}
	}

	public float getBatteryLevel() {
		return batteryLevel;
	}

	public String getBackupFile() {
		return backupFile.getName();
	}

	public File[] getBackupFiles() {
		return backupFiles;
	}

	public boolean isReadingTags() {
		return readingTags;
	}

	public boolean isPushingTags() {
		return pushingTags;
	}

	public boolean isBuzzerOn() {
		return buzzerOn;
	}

	public String getProtocol() {
		return protocol;
	}

	public int getBounce() {
		return bounce;
	}

	public void setDateTimeOffset(long dateTimeOffset) {
		this.dateTimeOffset = dateTimeOffset;
	}

	public void setVoltage(float voltage) {
		synchronized (this) {
			this.voltage = voltage;
		}
	}

	public void setBatteryLevel(float batteryLevel) {
		this.batteryLevel = batteryLevel;
	}

//	public void setBackupFile(String backupFile) {
//		this.backupFile = backupFile;
//	}

	public void setBackupFiles(File[] backupFiles) {
		this.backupFiles = backupFiles;
	}

	public void setReadingTags(boolean readingTags) {
		this.readingTags = readingTags;
	}

	public void setPushingTags(boolean pushingTags) {
		this.pushingTags = pushingTags;
	}

	public void setBuzzerOn(boolean buzzerOn) {
		this.buzzerOn = buzzerOn;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public void setBounce(int bounce) {
		this.bounce = bounce;
	}

	public void stopReading() {
		synchronized (this) {
			readingTags = false;
		}
	}

	public void startReading() throws IOException {
		synchronized (this) {
			readingTags = true;
			createNewLogFile();
		}
	}

	private void createNewLogFile() throws IOException {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
		String newFileName = dateFormat.format(date) + ".csv";
		String logFileName = backupPath.getAbsoluteFile() + "/" + newFileName;
		logger.info("Creating new log file: " + logFileName);
		backupFile = new File(logFileName);
		if (backupFile.exists()) {

		} else {
			backupFile.createNewFile();
		}

	}

	public void stopPushingTags() {
		synchronized (this) {
			pushingTags = false;
		}
	}

	public void startPushingTags() {
		synchronized (this) {
			pushingTags = true;
		}
	}

	public void setBuzzerOff() {
		synchronized (this) {
			buzzerOn = false;
		}
	}

	public void setBuzzerOn() {
		synchronized (this) {
			buzzerOn = true;
		}
	}

	public boolean isHasPower() {
		return hasPower;
	}

	public void setHasPower(boolean hasPower) {
		this.hasPower = hasPower;
	}

	public UserInterfaceListener getUiListener() {
		return uiListener;
	}

	public void setUiListener(UserInterfaceListener uiListener) {
		this.uiListener = uiListener;
	}

	public Date getReaderTime() {
		Date readerTime = new Date((new Date()).getTime() + dateTimeOffset);
		return readerTime;
	}

	public File getBackupPath() {
		return backupPath;
	}

	public File getLogFile() {
		return logFile;
	}

	public void setBackupPath(File backupPath) {
		this.backupPath = backupPath;
		loadBackupFiles();
	}

	private void loadBackupFiles() {
		backupFiles = backupPath.listFiles();
	}

	public void setLogFile(File logFile) {
		this.logFile = logFile;
	}

	public void setAntennaStatus(int antenna, boolean status) {
		switch (antenna) {
		case 1:
			antenna1 = status;
			break;
		case 2:
			antenna2 = status;
			break;
		case 3:
			antenna3 = status;
			break;
		case 4:
			antenna4 = status;
			break;
		case 5:
			antenna5 = status;
			break;
		case 6:
			antenna6 = status;
			break;
		case 7:
			antenna7 = status;
			break;
		case 8:
			antenna8 = status;
			break;
		}
	}

	public void logTagRead(String dataRow) {
		if (backupFile != null) {
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(backupFile, true));
				writer.write(dataRow);
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}

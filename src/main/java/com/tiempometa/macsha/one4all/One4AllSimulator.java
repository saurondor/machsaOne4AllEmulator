/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.tiempometa.macsha.one4all;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Random;

import org.apache.log4j.Logger;

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
import com.tiempometa.macsha.one4all.commands.PingCommand;
import com.tiempometa.macsha.one4all.commands.PushTagsCommand;
import com.tiempometa.macsha.one4all.commands.ReadBatteryCommand;
import com.tiempometa.macsha.one4all.commands.SetBounceCommand;
import com.tiempometa.macsha.one4all.commands.SetBuzzerCommand;
import com.tiempometa.macsha.one4all.commands.SetProtocolCommand;
import com.tiempometa.macsha.one4all.commands.SetTimeCommand;
import com.tiempometa.macsha.one4all.commands.StartCommand;
import com.tiempometa.macsha.one4all.commands.StopCommand;
import com.tiempometa.macsha.one4all.states.One4AllState;
import com.tiempometa.macsha.one4all.states.StopState;
import com.tiempometa.macsha.one4all.states.UserInterfaceListener;

/**
 * @author Gerardo Tasistro gtasistro@tiempometa.com Copyright 2015 Gerardo
 *         Tasistro Licensed under the Mozilla Public License, v. 2.0
 * 
 */
public class One4AllSimulator implements Runnable, CommandListener {

	public static final String COMMAND_GETPROTOCOL = "GETPROTOCOL";
	public static final String COMMAND_SETPROTOCOL = "SETPROTOCOL";
	public static final String COMMAND_START = "START";
	public static final String COMMAND_STOP = "STOP";
	public static final String COMMAND_PUSHTAGS = "PUSHTAGS";
	public static final String COMMAND_SETBOUNCE = "SETBOUNCE";
	public static final String COMMAND_SETTIME = "SETTIME";
	public static final String COMMAND_GETTIME = "GETTIME";
	public static final String COMMAND_SETBUZZER = "SETBUZZER";
	public static final String COMMAND_NEWFILE = "NEWFILE";
	public static final String COMMAND_PASSINGS = "PASSINGS";
	public static final String COMMAND_GETPASSINGS = "GETPASSINGS";
	public static final String COMMAND_READBATTERY = "READBATTERY";
	public static final String COMMAND_PING = "PING";
	public static final String COMMAND_LISTFILES = "LISTFILES";
	public static final String COMMAND_GETFILEINFO = "GETFILEINFO";
	public static final String COMMAND_GETFILE = "GETFILE";
	public static final String COMMAND_CLEARFILES = "CLEARFILES";
	public static final String COMMAND_GETSTATUS = "GETSTATUS";
	private static final String COMMAND_HELLO = "HELLO";

	private static final Logger logger = Logger.getLogger(One4AllSimulator.class);
	private ServerSocket serverSocket;
	private int port = 10002; // use default port
	private Socket clientSocket = null;
	private boolean transmitting = true;
//	private String response = null;
	Thread inputListenerThread;
	OutputStream outputStream;
	File backupPath;

	One4AllState state = new One4AllState();
	private Integer tagCount = 1;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			clientSocket = serverSocket.accept();
			try {
				outputStream = clientSocket.getOutputStream();
				inputListenerThread = new Thread(new InputListener(clientSocket.getInputStream(), this));
				inputListenerThread.start();
				Random rand = new Random();
				while (transmitting) {
					synchronized (this) {
						// commented out
						// TODO handle resend log files
//						if (response == null) {
//							for (int j = 0; j < 100000; j += 1) {
//								int random = 1 * (rand.nextInt(10) + 1);
//								try {
//									Thread.sleep(10000);
//								} catch (InterruptedException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
//								String code = String.format("%05d", j % 20);
//								String dataRow = null;
//								if ((j % 2) == 0) {
//									// if even row
//									dataRow = "6;DM;" + code + ";2018-10-02;12:44:06.696;1;1;45026\r\n";
//								} else {
//									dataRow = "DM" + code + "D20150414T120755416T120758010A1R034TV\r\n";
//								}
//								outputStream.write(dataRow.getBytes());
//								outputStream.flush();
//							}
//						} else {
//							outputStream.write(response.getBytes());
//							outputStream.write("\\n".getBytes());
//							outputStream.flush();
//							logger.debug("Sent data!");
//							try {
//								Thread.sleep(10000);
//							} catch (InterruptedException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//						}
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	@Override
	public void notifyCommand(String command) {
//		response = command;
		logger.info("Parsing command " + command);
		if (logger.isDebugEnabled()) {
			logger.debug("PARSE>\n" + command + "\nLEN:" + command.length());
		}
		String[] dataRows = command.split("\\r\\n");
		if (logger.isDebugEnabled()) {
			logger.debug("DATAROWS>\t:" + dataRows.length);
		}
		for (int i = 0; i < dataRows.length; i++) {
			String[] row = dataRows[i].split(";");
			parseCommandResponse(row);
		}
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void start() throws IOException {
		logger.info("Starting socket server");
		serverSocket = new ServerSocket(port);
		logger.info("Successfully started socket server on port :" + port);
	}

	private void parseCommandResponse(String[] row) {
		MacshaCommand command = null;
		switch (row[0]) {
		case COMMAND_CLEARFILES:
			logger.debug("GOT CLEARFILES COMMAND");
			command = new ClearFilesCommand();
			command.parseResponseRow(row);
			break;
		case COMMAND_GETFILE:
			logger.debug("GOT GETFILE COMMAND");
			command = new GetFileCommand();
			command.parseResponseRow(row);
			break;
		case COMMAND_GETFILEINFO:
			logger.debug("GOT GETFILEINFO COMMAND");
			command = new GetFileInfoCommand();
			command.parseResponseRow(row);
			break;
		case COMMAND_GETPASSINGS:
			logger.debug("GOT GETPASSINGS COMMAND");
			command = new GetPassingsCommand();
			command.parseResponseRow(row);
			break;
		case COMMAND_GETPROTOCOL:
			logger.debug("GOT GETPROTOCOL COMMAND");
			command = new GetProtocolCommand();
			command.parseResponseRow(row);
			break;
		case COMMAND_GETTIME:
			logger.debug("GOT GETTIME COMMAND");
			command = new GetTimeCommand();
			command.parseResponseRow(row);
			break;
		case COMMAND_HELLO:
			logger.debug("GOT HELLO COMMAND");
			break;
		case COMMAND_LISTFILES:
			logger.debug("GOT LISTFILES COMMAND");
			command = new ListFilesCommand();
			command.parseResponseRow(row);
			break;
		case COMMAND_NEWFILE:
			logger.debug("GOT NEWFILE COMMAND");
			command = new NewFileCommand();
			command.parseResponseRow(row);
			break;
		case COMMAND_PASSINGS:
			logger.debug("GOT PASSINGS COMMAND");
			command = new StartCommand();
			command.parseResponseRow(row);
			break;
		case COMMAND_PING:
			logger.debug("GOT PING COMMAND");
			command = new PingCommand();
			command.parseResponseRow(row);
			break;
		case COMMAND_PUSHTAGS:
			logger.debug("GOT PUSHTAGS COMMAND");
			command = new PushTagsCommand();
			command.parseResponseRow(row);
			break;
		case COMMAND_READBATTERY:
			logger.debug("GOT READBATTERY COMMAND");
			command = new ReadBatteryCommand();
			command.parseResponseRow(row);
			break;
		case COMMAND_SETBOUNCE:
			logger.debug("GOT SETBOUNCE COMMAND");
			command = new SetBounceCommand();
			command.parseResponseRow(row);
			break;
		case COMMAND_SETBUZZER:
			logger.debug("GOT SETBUZZER COMMAND");
			command = new SetBuzzerCommand();
			command.parseResponseRow(row);
			break;
		case COMMAND_SETPROTOCOL:
			logger.debug("GOT SETPROTOCOL COMMAND");
			command = new SetProtocolCommand();
			command.parseResponseRow(row);
			break;
		case COMMAND_SETTIME:
			logger.debug("GOT SETTIME COMMAND");
			command = new SetTimeCommand();
			command.parseCommandRow(row);
			break;
		case COMMAND_START:
			logger.debug("GOT START COMMAND");
			command = new StartCommand();
			command.parseCommandRow(row);
			break;
		case COMMAND_STOP:
			logger.debug("GOT STOP COMMAND");
			command = new StopCommand();
			command.parseCommandRow(row);
			break;
		case COMMAND_GETSTATUS:
			logger.debug("GOT STOP GETSTATUS");
			command = new GetStatusCommand();
			command.parseCommandRow(row);
			break;
		default:
			break;
		}
		handleCommandResponse(command);
	}

	private void handleCommandResponse(MacshaCommand command) {
		if (command == null) {

		} else {
			MacshaCommand response = state.handleCommand(command);
			if (response != null) {
				try {
					response.sendResponse(outputStream);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public One4AllState getState() {
		return state;
	}

	public void setUIListener(UserInterfaceListener uiListener) {
		state.setUiListener(uiListener);
	}

	public void sendCommand(MacshaCommand command) throws IOException {
		command.sendCommand(outputStream);
	}

	public void sendTagRead(String tagString) throws IOException {
		String id = tagString.substring(0, 2);
		String tag = tagString.substring(2, 7);
		String dateString = (new SimpleDateFormat("yyyy-MM-dd;HH:mm:ss.SSS")).format(getState().getReaderTime());
		String dataRow = tagCount + ";" + id + ";" + tag + ";" + dateString + ";1;1;45026\r\n";
		if (outputStream != null) {
			try {
				outputStream.write(dataRow.getBytes());
				outputStream.flush();
			} catch (IOException e) {
				throw e;
			}
		}
		state.logTagRead(dataRow);
		tagCount = tagCount + 1;
	}

	public void setBackupPath(File backupPath) {
		state.setBackupPath(backupPath);
	}

	public void setAcPowerOn() {
		state.setHasPower(true);
	}

	public void setAcPowerOff() {
		state.setHasPower(false);
	}

	public void setAntennaStatus(int antenna, boolean status) {
		state.setAntennaStatus(antenna, status);
	}
}

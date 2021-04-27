/**
 * 
 */
package com.tiempometa.macsha.one4all.commands;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author gtasi
 *
 */
public abstract class MacshaCommand {

	public static final int STATUS_OK = 0;
	public static final int STATUS_ERROR = 1;
	public static final int STATUS_NO_RESPONSE = -1;

	private String errorCode;
	private Integer status = STATUS_NO_RESPONSE;

	public boolean succeeded() {
		return status.equals(STATUS_OK);
	}

	public static final String CLRF = "\r\n";
	public static final String RESPONSE_LENGTH_ERROR = "LENGTH ERROR";
	public static final String RESPONSE_ERR = "ERR";
	public static final String RESPONSE_UNKNOWN = "UNKNOWN";
	public static final String RESPONSE_STARTMODE = "STARTMODE";
	public static final String RESPONSE_ERRSTART = "ERRSTART";
	public static final String RESPONSE_ERRCONNECT = "ERRCONNECT";
	public static final String RESPONSE_ERRANTENNAS = "ERRANTENNAS";
	public static final String RESPONSE_ERRSTOP = "ERRSTOP";
	public static final String RESPONSE_ERRCLOSE = "ERRCLOSE";
	public static final String RESPONSE_OK = "OK";
	public static final String RESPONSE_FALSE = "false";
	public static final String RESPONSE_TRUE = "true";
	public static final String RESPONSE_ERRFILE = "ERRFILE";
	public static final String RESPONSE_STOPTMODE = "STOPTMODE";
	public static final String RESPONSE_NOFILES = "NOFILES";
	public static final String RESPONSE_FILENOTFOUND = "FILENOTFOUND";
	public static final String RESPONSE_PONG = "PONG";

	public abstract void parseCommandRow(String[] row);

	public abstract void parseResponseRow(String[] row);

	public abstract void sendCommand(OutputStream dataOutputStream) throws IOException;

	public abstract void sendResponse(OutputStream dataOutputStream) throws IOException;

	public String getErrorCode() {
		return errorCode;
	}

	public Integer getStatus() {
		return status;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}

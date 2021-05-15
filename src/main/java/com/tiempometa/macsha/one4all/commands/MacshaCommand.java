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
package com.tiempometa.macsha.one4all.commands;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Gerardo Esteban Tasistro Giubetic
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

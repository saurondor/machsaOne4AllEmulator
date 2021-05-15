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

import org.apache.log4j.Logger;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 *
 */
public class StartCommand extends MacshaCommand {

	private static final Logger logger = Logger.getLogger(StartCommand.class);

	// Start.
	// Para comenzar la lectura de los chips, el host envía START<CrLf>
	//
	// El One4All responde:
	// START;<Response><CrLf>
	//
	// Donde <Response> es:
	// En el éxito, la fecha y hora del inicio de la sesión de cronometraje y el
	// nombre del archivo de backup de la sesión con el siguiente formato:
	// yyyyMMdd-HHmmss.csv
	// STARTMODE, si el sistema ya se encuentra en Start mode.
	// ERRANTENNAS, si no hay antenas conectadas al sistema.
	// ERRCONNECT, ERRSTART, ERR, si ocurre algún otro error durante el proceso de
	// inicio.
	//
	// Ejemplo:
	// < START<CrLf>
	// > START;20171223-153055.csv<CrLf>

	private String logFileName;

	@Override
	public void parseResponseRow(String[] row) {
		if (row.length > 1) {
			switch (row[1]) {
			case RESPONSE_STARTMODE:
				setErrorCode(RESPONSE_STARTMODE);
				setStatus(STATUS_ERROR);
				break;
			case RESPONSE_ERRANTENNAS:
				setErrorCode(RESPONSE_ERRANTENNAS);
				setStatus(STATUS_ERROR);
				break;
			case RESPONSE_ERRCONNECT:
				setErrorCode(RESPONSE_STARTMODE);
				setStatus(STATUS_ERROR);
				break;
			case RESPONSE_ERRSTART:
				setErrorCode(RESPONSE_ERRSTART);
				setStatus(STATUS_ERROR);
				break;
			case RESPONSE_ERR:
				setErrorCode(RESPONSE_ERR);
				setStatus(STATUS_ERROR);
				break;
			default:
				logFileName = row[1];
				setStatus(STATUS_OK);
				break;
			}
		} else {
			setErrorCode(RESPONSE_LENGTH_ERROR);
			setStatus(STATUS_ERROR);
		}
	}

	@Override
	public void sendCommand(OutputStream dataOutputStream) throws IOException {
		dataOutputStream.write("PUSHTAGS;true\r\n".getBytes());
		dataOutputStream.flush();
	}

	public String getLogFileName() {
		return logFileName;
	}

	@Override
	public void parseCommandRow(String[] row) {
	}

	@Override
	public void sendResponse(OutputStream dataOutputStream) throws IOException {
		String payload = "START;" + logFileName + "\r\n";
		dataOutputStream.write(payload.getBytes());
		dataOutputStream.flush();
	}

	public void setLogFileName(String logFileName) {
		this.logFileName = logFileName;
	}

}

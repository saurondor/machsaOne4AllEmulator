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
public class NewFileCommand extends MacshaCommand {

	private static final Logger logger = Logger.getLogger(NewFileCommand.class);

	// Con el fin de crear un nuevo archivo sin parar el Start mode, el host envía
	// NEWFILE<CrLf>. Esta operación puede ser realizada solamente en Start mode.
	//
	// El One4All responde:
	// NEWFILE;<Response><CrLf>
	//
	// Donde <Response> es:
	// En el éxito, la fecha y hora del nuevo archivo de sesión de cronometraje y el
	// nombre del nuevo archivo de backup de la sesión con el siguiente formato:
	// yyyyMMdd-HHmmss.csv
	// STOPTMODE, si el sistema se encuentra en Stop mode.
	// ERRFILE, ERR, si ocurre algún otro error durante el proceso.
	//
	// Ejemplo:
	// < NEWFILE<CrLf>
	// > NEWFILE;20171223-153055.csv<CrLf>

	private String fileName;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tiempometa.pandora.macshareader.commands.MacshaCommand#parseCommandRow(
	 * java.lang.String[])
	 */
	@Override
	public void parseResponseRow(String[] row) {
		if (row.length > 1) {
			switch (row[1]) {
			case RESPONSE_STOPTMODE:
				setErrorCode(RESPONSE_STOPTMODE);
				setStatus(STATUS_ERROR);
				break;
			case RESPONSE_ERRFILE:
				setErrorCode(RESPONSE_ERRFILE);
				setStatus(STATUS_ERROR);
				break;
			case RESPONSE_ERR:
				setErrorCode(RESPONSE_ERR);
				setStatus(STATUS_ERROR);
				break;
			default:
				logger.debug("New file name = " + row[1]);
				fileName = row[1];
				setStatus(STATUS_OK);
				break;
			}
		} else {
			setErrorCode(RESPONSE_LENGTH_ERROR);
			setStatus(STATUS_ERROR);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tiempometa.pandora.macshareader.commands.MacshaCommand#sendCommand(java.
	 * io.OutputStream)
	 */
	@Override
	public void sendCommand(OutputStream dataOutputStream) throws IOException {
		dataOutputStream.write("NEWFILE\r\n".getBytes());
		dataOutputStream.flush();
	}

	public String getFileName() {
		return fileName;
	}

	@Override
	public void parseCommandRow(String[] row) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendResponse(OutputStream dataOutputStream) throws IOException {
		// TODO Auto-generated method stub
		
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}

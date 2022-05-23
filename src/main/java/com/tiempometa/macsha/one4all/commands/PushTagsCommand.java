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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 *
 */
public class PushTagsCommand extends MacshaCommand {

	private static final Logger logger = LogManager.getLogger(PushTagsCommand.class);

	// Con el fin de habilitar/deshabilitar la transmisión en vivo de las pasadas al
	// host, el host envía PUSHTAGS;<Status><CrLf>. Por defecto, el Push mode está
	// deshabilitado. Las pasadas serán enviadas a la última conexión activa
	// solamente.
	//
	// Donde <Status> es:
	// true, para habilitar el Push mode.
	// false, para deshabilitar el Push mode.
	//
	// El One4All responde:
	// PUSHTAGS;<Response><CrLf>
	//
	// Donde <Response> es:
	// true, false, en el éxito.
	// ERR, si ocurrió algún error.
	//
	// Ejemplo:
	// < PUSHTAGS;true<CrLf>
	// > PUSHTAGS;true<CrLf>

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tiempometa.pandora.macshareader.commands.MacshaCommand#parseCommandRow(
	 * java.lang.String[])
	 */

	private boolean pushStatus = true;

	public PushTagsCommand() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PushTagsCommand(boolean pushStatus) {
		super();
		this.pushStatus = pushStatus;
	}

	@Override
	public void parseResponseRow(String[] row) {
		if (row.length > 1) {
			switch (row[1]) {
			case RESPONSE_TRUE:
				pushStatus = true;
				setStatus(STATUS_OK);
				break;
			case RESPONSE_FALSE:
				pushStatus = false;
				setStatus(STATUS_OK);
				break;
			case RESPONSE_ERR:
				setErrorCode(RESPONSE_ERR);
				setStatus(STATUS_ERROR);
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
		String payload = "PUSHTAGS;" + pushStatus + "\r\n";
		dataOutputStream.write(payload.getBytes());
		dataOutputStream.flush();
	}

	public boolean isPushStatus() {
		return pushStatus;
	}

	@Override
	public void parseCommandRow(String[] row) {
		if (row.length > 1) {
			switch (row[1]) {
			case RESPONSE_FALSE:
				pushStatus = false;
				break;
			case RESPONSE_TRUE:
				pushStatus = true;
				break;
			default:
				pushStatus = false;
				break;
			}
		}
	}

	@Override
	public void sendResponse(OutputStream dataOutputStream) throws IOException {
		String payload = "PUSHTAGS;" + pushStatus + "\r\n";
		dataOutputStream.write(payload.getBytes());
		dataOutputStream.flush();
	}

}

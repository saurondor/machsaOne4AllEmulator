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
public class ListFilesCommand extends MacshaCommand {

	private static final Logger logger = Logger.getLogger(ListFilesCommand.class);

	// Para listar los archivos de backup CSV (pasadas) disponibles en el sistema,
	// el host envía LISTFILES;<CrLf>.
	//
	// El One4All responde:
	// LISTFILES;<FileList><CrLf>
	//
	// Donde <FileList> es:
	// En el éxito, los nombres de los archivos de backup disponibles, con el
	// siguiente formato: yyyyMMdd-HHmmss.csv
	// NOFILES, si no hay archivos en la memoria.
	// ERR, si ocurre algún error durante el proceso.
	//
	// Ejemplo:
	// < LISTFILES<CrLf>
	// >
	// LISTFILES;20171222-093055.csv;20171223-183156.csv;20171224-112235.csv;<CrLf>

	private String[] files = new String[0];

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
			case RESPONSE_NOFILES:
				setErrorCode(RESPONSE_NOFILES);
				setStatus(STATUS_ERROR);
				break;
			case RESPONSE_ERR:
				setErrorCode(RESPONSE_ERR);
				setStatus(STATUS_ERROR);
				break;
			default:
				logger.debug("*** listing files:");
				files = new String[row.length - 1];
				for (int i = 1; i < row.length; i++) {
					logger.debug("\t" + row[i]);
					files[i - 1] = row[i];
				}
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
		dataOutputStream.write("LISTFILES\r\n".getBytes());
		dataOutputStream.flush();
	}

	@Override
	public void parseCommandRow(String[] row) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendResponse(OutputStream dataOutputStream) throws IOException {
		StringBuffer response = new StringBuffer("LISTFILES;");
		if (files.length == 0) {
			response.append("NOFILES");
		} else {
			for (String fileName : files) {
				response.append(fileName);
				response.append(";");
			}
		}
		response.append("\r\n");
		dataOutputStream.write(response.toString().getBytes());
		dataOutputStream.flush();
	}

	public String[] getFiles() {
		return files;
	}

	public void setFiles(String[] files) {
		this.files = files;
	}

}

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
public class GetFileInfoCommand extends MacshaCommand {

	private static final Logger logger = Logger.getLogger(GetFileInfoCommand.class);

	// Para traer la información específica de un cierto archivo de backup, el host
	// envía GETFILEINFO;<FileName><CrLf>.
	//
	// Donde <FileName>, es el nombre del archivo de backup, con el formato
	// yyyyMMdd-HHmmss.csv.
	//
	// El One4All responde:
	// GETFILEINFO;<Response><CrLf>
	//
	// Donde <Response> es:
	// <FirstPassing>;<LastPassing>;<TotalPassings>, son:
	// <FirstPassing>, es la fecha y hora de la primer pasada en el archivo con el
	// formato yyyy-MM-dd;HH:mm:ss.kkk.
	// <LastPassing>, es la fecha y hora de la última pasada en el archivo con el
	// formato yyyy-MM-dd;HH:mm:ss.kkk.
	// <TotalPassings>, es la cantidad total de pasadas en el archivo.
	// FILENOTFOUND, si el archivo no existe.
	// ERR, si ocurre algún error durante el proceso.
	//
	// Ejemplo:
	// < GETFILEINFO;20171222-093055.csv<CrLf>
	// > GETFILEINFO;2017-12-22;09:30:58.967;2017-12-22;11:31:38.888;980<CrLf>

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tiempometa.pandora.macshareader.commands.MacshaCommand#parseCommandRow(
	 * java.lang.String[])
	 */

	private String fileName;
	private String firstPass;
	private String lastPass;
	private Integer totalPassings;

	public GetFileInfoCommand(String fileName) {
		super();
		this.fileName = fileName;
	}

	public GetFileInfoCommand() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void parseResponseRow(String[] row) {
		if (row.length > 1) {
			switch (row[1]) {
			case RESPONSE_FILENOTFOUND:
				setErrorCode(RESPONSE_FILENOTFOUND);
				setStatus(STATUS_ERROR);
				break;
			case RESPONSE_ERR:
				setErrorCode(RESPONSE_ERR);
				setStatus(STATUS_ERROR);
				break;
			default:
				logger.debug(
						"File info - first pass: " + row[1] + " last pass: " + row[2] + " total passings: " + row[3]);
				firstPass = row[1];
				lastPass = row[2];
				try {
					totalPassings = Integer.valueOf(row[3]);
				} catch (NumberFormatException e) {
					setErrorCode(RESPONSE_LENGTH_ERROR);
					setStatus(STATUS_ERROR);
					return;
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
		String payload = "GETFILE;" + fileName + "\r\n";
		dataOutputStream.write(payload.getBytes());
		dataOutputStream.flush();
	}

	public String getFileName() {
		return fileName;
	}

	public String getFirstPass() {
		return firstPass;
	}

	public String getLastPass() {
		return lastPass;
	}

	public Integer getTotalPassings() {
		return totalPassings;
	}

	@Override
	public void parseCommandRow(String[] row) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendResponse(OutputStream dataOutputStream) throws IOException {
		// TODO Auto-generated method stub
		
	}

}

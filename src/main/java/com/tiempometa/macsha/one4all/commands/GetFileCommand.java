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
public class GetFileCommand extends MacshaCommand {

	private static final Logger logger = LogManager.getLogger(GetFileCommand.class);

	// Para obtener las pasadas específicas de un cierto archivo de backup, el host
	// envía GETFILE;<FileName>;<StartPassing>;<EndPassing><CrLf>.
	//
	// Donde:
	// <FileName>, es el nombre del archivo de backup, con el formato
	// yyyyMMdd-HHmmss.csv.
	// <StartPassing>, es el número inicial de las pasadas a enviar.
	// <EndPassing>, es el número final de las pasadas a enviar.
	//
	// Ejemplo:
	// < GETFILE;20171222-093055.csv;15;17<CrLf>
	// > 15;MC;00001;2017-04-23;13:30:45.492;3;1;Z1y39<CrLf>
	// 16;MC;00002;2017-04-23;13:30:46.543;4;1;O90ut<CrLf>
	// 17;MC;00015;2017-04-23;13:30:46.993;4;1;kle23<CrLf>
	// <CrLf>
	//

	private String fileName;

	public GetFileCommand(String fileName) {
		super();
		this.fileName = fileName;
	}

	public GetFileCommand() {
		super();
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tiempometa.pandora.macshareader.commands.MacshaCommand#parseCommandRow(
	 * java.lang.String[])
	 */
	@Override
	public void parseResponseRow(String[] row) {
		// TODO Auto-generated method stub
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

	@Override
	public void parseCommandRow(String[] row) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendResponse(OutputStream dataOutputStream) throws IOException {
		// TODO Auto-generated method stub

	}

}

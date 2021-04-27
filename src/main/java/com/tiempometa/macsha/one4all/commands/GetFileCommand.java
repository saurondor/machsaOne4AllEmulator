/**
 * 
 */
package com.tiempometa.macsha.one4all.commands;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;

/**
 * @author gtasi
 *
 */
public class GetFileCommand extends MacshaCommand {

	private static final Logger logger = Logger.getLogger(GetFileCommand.class);

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

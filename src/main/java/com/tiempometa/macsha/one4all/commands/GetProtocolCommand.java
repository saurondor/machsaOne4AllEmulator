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
public class GetProtocolCommand extends MacshaCommand {

	private static final Logger logger = Logger.getLogger(GetProtocolCommand.class);

	// Para obtener la versión de protocolo en el sistema, el host envía
	// GETPROTOCOL<CrLf>
	//
	// El One4All responde:
	// GETPROTOCOL;<Current><Min><Max><CrLf>
	//
	// Donde:
	// <Current>, es la versión actual del protocolo.
	// <Min>, es la versión mínima del protocolo soportada por el sistema.
	// <Max>, is the maximal protocol version supported by the system.
	//
	// Ejemplo:
	// < GETPROTOCOL<CrLf>
	// > GETPROTOCOL;3.1;1.0;2.0<CrLf>

	private String currentVersion;
	private String minVersion;
	private String maxVersion;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tiempometa.pandora.macshareader.commands.MacshaCommand#parseCommandRow(
	 * java.lang.String[])
	 */
	@Override
	public void parseResponseRow(String[] row) {
		if (row.length > 3) {
			logger.debug("Protocol is current:" + row[1] + " min " + row[2] + " max " + row[3]);
			currentVersion = row[1];
			minVersion = row[2];
			maxVersion = row[3];
			setStatus(STATUS_OK);
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
		dataOutputStream.write("GETPROTOCOL\r\n".getBytes());
		dataOutputStream.flush();
	}

	public String getCurrentVersion() {
		return currentVersion;
	}

	public String getMinVersion() {
		return minVersion;
	}

	public String getMaxVersion() {
		return maxVersion;
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

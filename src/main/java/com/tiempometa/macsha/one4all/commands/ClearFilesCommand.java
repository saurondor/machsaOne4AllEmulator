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
public class ClearFilesCommand extends MacshaCommand {

	private static final Logger logger = Logger.getLogger(ClearFilesCommand.class);

	// Para eliminar todos los archivos de backup disponibles en la memoria, el host
	// envía CLEARFILES<CrLf>. Esta operación puede ser realizada solamente en Stop
	// mode.
	//
	// El One4All responde:
	// CLEARFILES;<Response><CrLf>
	//
	// Donde <Response> es:
	// OK, en el éxito.
	// ERR, si ocurre algún error durante el proceso.
	//
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
			case RESPONSE_OK:
				setStatus(STATUS_OK);
				break;
			case RESPONSE_ERR:
				setErrorCode(RESPONSE_ERR);
				setStatus(STATUS_ERROR);
				break;
			default:
				setErrorCode(RESPONSE_LENGTH_ERROR);
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
		dataOutputStream.write("CLEARFILES\r\n".getBytes());
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

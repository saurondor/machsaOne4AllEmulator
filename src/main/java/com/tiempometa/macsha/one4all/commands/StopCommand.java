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
public class StopCommand extends MacshaCommand {

	private static final Logger logger = Logger.getLogger(StopCommand.class);

	// Para finalizar la lectura de los chips, el host envía STOP<CrLf>
	//
	// El One4All responde:
	// STOP;<Response><CrLf>
	//
	// Donde <Response> es:
	// OK, en el éxito.
	// STOPMODE, si el sistema ya esta en Stop mode.
	// ERRCLOSE, ERRSTOP, ERR, si ocurre algún otro error durante el proceso de
	// parada.
	//
	// Ejemplo:
	// < STOP<CrLf>
	// > STOP;OK<CrLf>

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
			case RESPONSE_STARTMODE:
				setErrorCode(RESPONSE_STARTMODE);
				setStatus(STATUS_ERROR);
				break;
			case RESPONSE_OK:
				setStatus(STATUS_OK);
				break;
			case RESPONSE_ERRCLOSE:
				setErrorCode(RESPONSE_ERRCLOSE);
				setStatus(STATUS_ERROR);
				break;
			case RESPONSE_ERRSTOP:
				setErrorCode(RESPONSE_ERRSTOP);
				setStatus(STATUS_ERROR);
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
		dataOutputStream.write("STOP\r\n".getBytes());
		dataOutputStream.flush();
	}

	@Override
	public void parseCommandRow(String[] row) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendResponse(OutputStream dataOutputStream) throws IOException {
		String payload = "STOP;OK\r\n";
		dataOutputStream.write(payload.getBytes());
		dataOutputStream.flush();
	}

}

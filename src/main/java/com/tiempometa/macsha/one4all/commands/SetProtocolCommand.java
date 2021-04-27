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
public class SetProtocolCommand extends MacshaCommand {

	private static final Logger logger = Logger.getLogger(SetProtocolCommand.class);

	// Para configurar la versión de protocolo en el sistema, el host envía
	// SETPROTOCOL<Version><CrLf>. Esta operación puede ser realizada solamente en
	// Stop mode.
	//
	// El One4All responde:
	// SETPROTOCOL;<Response><CrLf>
	//
	// Donde <Response> es:
	// <Version>, en el éxito, la versión actual del protocolo.
	// STARTMODE, si el sistema esta en Start mode.
	// UNKNOWN, si la versión solicitada no existe o no esta disponible.
	// ERR, si ocurrió algún error.
	//
	// Ejemplo:
	// < SETPROTOCOL;3.0<CrLf>
	// > SETPROTOCOL;3.0<CrLf>

	private String protocolVersion;

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
			case RESPONSE_UNKNOWN:
				setErrorCode(RESPONSE_UNKNOWN);
				setStatus(STATUS_ERROR);
				break;
			case RESPONSE_ERR:
				setErrorCode(RESPONSE_ERR);
				setStatus(STATUS_ERROR);
				break;
			default:
				protocolVersion = row[1];
				setStatus(STATUS_OK);
				logger.debug("Protocol version " + protocolVersion);
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
		String protocol = "3.0";
		String payload = "SETPROTOCOL;" + protocol + "\r\n";
		dataOutputStream.write(payload.getBytes());
		dataOutputStream.flush();
	}

	public String getProtocolVersion() {
		return protocolVersion;
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

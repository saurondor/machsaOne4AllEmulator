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
public class SetBuzzerCommand extends MacshaCommand {

	private static final Logger logger = Logger.getLogger(SetBuzzerCommand.class);

	// Con el fin de habilitar/deshabilitar el buzzer y la señal lumínica, el host
	// envía SETBUZZER;<Status><CrLf>.
	//
	// Donde <Status> es:
	// true, para habilitar el buzzer.
	// false, para deshabilitar el buzzer.
	//
	// El One4All responde:
	// SETBUZZER;<Response><CrLf>
	//
	// Donde <Response> es:
	// true, false, en el éxito.
	// ERR, si ocurrió algún error.
	//
	// Ejemplo:
	// < SETBUZZER;true<CrLf>
	// > SETBUZZER;true<CrLf>

	private boolean buzzerStatus = true;

	public SetBuzzerCommand(boolean buzzerStatus) {
		super();
		this.buzzerStatus = buzzerStatus;
	}

	public SetBuzzerCommand() {
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
		if (row.length > 1) {
			switch (row[1]) {
			case RESPONSE_TRUE:
				buzzerStatus = true;
				setStatus(STATUS_OK);
				break;
			case RESPONSE_FALSE:
				buzzerStatus = false;
				setStatus(STATUS_OK);
				break;
			case "ERR":
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
		String payload = "SETBUZZER;" + buzzerStatus + "\r\n";
		dataOutputStream.write(payload.getBytes());
		dataOutputStream.flush();
	}

	public boolean isBuzzerStatus() {
		return buzzerStatus;
	}

	@Override
	public void parseCommandRow(String[] row) {
		if (row.length > 1) {
			switch (row[1]) {
			case RESPONSE_FALSE:
				buzzerStatus = false;
				break;
			case RESPONSE_TRUE:
				buzzerStatus = true;
				break;
			default:
				buzzerStatus = false;
				break;
			}
		}
	}

	@Override
	public void sendResponse(OutputStream dataOutputStream) throws IOException {
		String payload = "SETBUZZER;" + buzzerStatus + "\r\n";
		dataOutputStream.write(payload.getBytes());
		dataOutputStream.flush();
	}

}

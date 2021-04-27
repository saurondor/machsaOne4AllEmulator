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
public class SetBounceCommand extends MacshaCommand {

	private static final Logger logger = Logger.getLogger(SetBounceCommand.class);

	// Es el tiempo muerto de detección o por cuánto tiempo el sistema debe ignorar
	// un chip después de una detección. Para establecer el tiempo de rebote o el
	// tiempo muerto, el host envía SETBOUNCE;<Seconds><CrLf>. Por defecto, el
	// tiempo de rebote es de 5 segundos.
	//
	// Donde <Seconds> es desde 1 hasta 3600.
	//
	// El One4All responde:
	// SETBOUNCE;<Response><CrLf>
	//
	// Donde <Response> es:
	// 1 a 3600, en el éxito.
	// ERR, si ocurrió algún error.
	//
	// Ejemplo:
	// < SETBOUNCE;60<CrLf>
	// > SETBOUNCE;60<CrLf>

	private Integer bounce;

	public SetBounceCommand(Integer bounce) {
		super();
		this.bounce = bounce;
	}

	public SetBounceCommand() {
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
			case RESPONSE_ERR:
				setErrorCode(RESPONSE_ERR);
				setStatus(STATUS_ERROR);
				break;
			default:
				try {
					bounce = Integer.valueOf(row[1]);
				} catch (NumberFormatException e) {
					setErrorCode(RESPONSE_LENGTH_ERROR);
					setStatus(STATUS_ERROR);
				}
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
		String payload = "SETBOUNCE;" + bounce + "\r\n";
		dataOutputStream.write(payload.getBytes());
		dataOutputStream.flush();

	}

	public Integer getBounce() {
		return bounce;
	}

	@Override
	public void parseCommandRow(String[] row) {
		if (row.length > 1) {
			try {
				bounce = Integer.valueOf(row[1]);
			} catch (NumberFormatException e) {
				setErrorCode(RESPONSE_LENGTH_ERROR);
				setStatus(STATUS_ERROR);
			}
		} else {
			setErrorCode(RESPONSE_LENGTH_ERROR);
			setStatus(STATUS_ERROR);
		}
	}

	@Override
	public void sendResponse(OutputStream dataOutputStream) throws IOException {
		// TODO Auto-generated method stub

	}

}

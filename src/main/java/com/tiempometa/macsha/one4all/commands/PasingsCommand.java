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
public class PasingsCommand extends MacshaCommand {

	private static final Logger logger = Logger.getLogger(PasingsCommand.class);

	// Con el fin de recibir el número de pasadas y los últimos ocho chips leídos de
	// la sesión de cronometraje actual, el host envía PASSINGS<CrLf>.
	//
	// El One4All responde:
	// PASSINGS;<Number>;<Last eight><CrLf>
	//
	// Donde:
	// <Number>, es el número de pasadas actuales de la sesión de cronometraje.
	// <Last eight>, es:
	// los últimos ocho chips leídos con el formato: MC00001;MC00002;MC00003;…
	// ERR, si ocurre algún error durante el proceso.
	//
	// Ejemplo:
	// < PASSINGS<CrLf>
	// >
	// PASSINGS;190;MC00001;MC00002;MC00003;MC00017;MC00022;MC00033;MC00012;MC00190<CrLf>

	private String[] passings;
	private String passCount;

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
			passCount = row[1];
			passings = new String[row.length - 2];
			for (int i = 2; i < row.length; i++) {
				logger.debug("Passing " + row[i]);
				passings[i - 1] = row[i];
			}
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
		dataOutputStream.write("PASSINGS\r\n".getBytes());
		dataOutputStream.flush();
	}

	public String[] getPassings() {
		return passings;
	}

	public String getPassCount() {
		return passCount;
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

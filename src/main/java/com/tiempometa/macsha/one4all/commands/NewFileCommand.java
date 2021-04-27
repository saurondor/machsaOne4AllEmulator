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
public class NewFileCommand extends MacshaCommand {

	private static final Logger logger = Logger.getLogger(NewFileCommand.class);

	// Con el fin de crear un nuevo archivo sin parar el Start mode, el host envía
	// NEWFILE<CrLf>. Esta operación puede ser realizada solamente en Start mode.
	//
	// El One4All responde:
	// NEWFILE;<Response><CrLf>
	//
	// Donde <Response> es:
	// En el éxito, la fecha y hora del nuevo archivo de sesión de cronometraje y el
	// nombre del nuevo archivo de backup de la sesión con el siguiente formato:
	// yyyyMMdd-HHmmss.csv
	// STOPTMODE, si el sistema se encuentra en Stop mode.
	// ERRFILE, ERR, si ocurre algún otro error durante el proceso.
	//
	// Ejemplo:
	// < NEWFILE<CrLf>
	// > NEWFILE;20171223-153055.csv<CrLf>

	private String fileName;

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
			case RESPONSE_STOPTMODE:
				setErrorCode(RESPONSE_STOPTMODE);
				setStatus(STATUS_ERROR);
				break;
			case RESPONSE_ERRFILE:
				setErrorCode(RESPONSE_ERRFILE);
				setStatus(STATUS_ERROR);
				break;
			case RESPONSE_ERR:
				setErrorCode(RESPONSE_ERR);
				setStatus(STATUS_ERROR);
				break;
			default:
				logger.debug("New file name = " + row[1]);
				fileName = row[1];
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
		dataOutputStream.write("NEWFILE\r\n".getBytes());
		dataOutputStream.flush();
	}

	public String getFileName() {
		return fileName;
	}

	@Override
	public void parseCommandRow(String[] row) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendResponse(OutputStream dataOutputStream) throws IOException {
		// TODO Auto-generated method stub
		
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}

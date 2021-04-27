/**
 * 
 */
package com.tiempometa.macsha.one4all.commands;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * @author gtasi
 *
 */
public class SetTimeCommand extends MacshaCommand {

	private static final Logger logger = Logger.getLogger(SetTimeCommand.class);

	// Con el fin de configurar la fecha y hora interna del sistema, el host envía
	// SETTIME;yyyy-MM-dd;HH:mm:ss<CrLf>. Esta operación puede ser realizada
	// solamente en Stop mode.
	//
	// El One4All responde:
	// SETTIME;<Response><CrLf>
	//
	// Donde <Response> es:
	// En el éxito, la fecha y hora configurada con el siguiente formato:
	// yyyy-MM-dd;HH:mm:ss
	// STARTMODE, si el sistema esta en Start mode.
	// ERR, si ocurrió algún error.
	//
	// Ejemplo:
	// < SETTIME;2017-12-23;15:30:55<CrLf>
	// > SETTIME;2017-12-23;15:30:55<CrLf>

	private String timeString;
	private Date dateTime;

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
			case RESPONSE_ERR:
				setErrorCode(RESPONSE_ERR);
				setStatus(STATUS_ERROR);
				break;
			default:
				timeString = row[1];
				setStatus(STATUS_OK);
				logger.debug("Reader time is :" + timeString);
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
		Date dateTime = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		String date = dateFormat.format(dateTime);
		String time = timeFormat.format(dateTime);
		String payload = "SETTIME;" + date + ";" + time + "\r\n";
		dataOutputStream.write(payload.getBytes());
		dataOutputStream.flush();
	}

	public String getTimeString() {
		return timeString;
	}

	@Override
	public void parseCommandRow(String[] row) {
		if (row.length > 2) {
			String date = row[1];
			String time = row[2];
			try {
				timeString = date + ";" + time;
				dateTime = (new SimpleDateFormat("yyyy-MM-dd;HH:mm:ss")).parse(timeString);
			} catch (ParseException e) {
				dateTime = null;
				e.printStackTrace();
			}
		}
	}

	@Override
	public void sendResponse(OutputStream dataOutputStream) throws IOException {
		String payload = "SETTIME;" + timeString + "\r\n";
		dataOutputStream.write(payload.getBytes());
		dataOutputStream.flush();
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

}

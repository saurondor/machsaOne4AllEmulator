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
public class ReadBatteryCommand extends MacshaCommand {

	private static final Logger logger = Logger.getLogger(ReadBatteryCommand.class);

	// Con el fin de recibir el estado de la batería interna, el host envía
	// READBATTERY;<CrLf>.
	//
	// El One4All responde:
	// READBATTERY;VOLTS<Volts>;PERCENT<Percent>;HASPOWER<HasPower><CrLf>
	//
	// Donde:
	// <Volts>, es el voltaje actual de la batería. 26,5 Volts es el valor máximo.
	// <Percent>, es la carga actual en porcentaje de la batería. Desde 0% hasta
	// 100%.
	// <HasPower>, es:
	// true, si el sistema esta conectado a la red eléctrica y cargando.
	// false, si el sistema no esta conectado a la red eléctrica.
	//
	// Ejemplo:
	// < READBATTERY<CrLf>
	// > READBATTERY;VOLTS;25.5;PERCENT;90;HASPOWER;false<CrLf>

	private Float voltage;
	private Float charge;
	private boolean hasPower;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tiempometa.pandora.macshareader.commands.MacshaCommand#parseCommandRow(
	 * java.lang.String[])
	 */
	@Override
	public void parseResponseRow(String[] row) {
		if (row.length > 6) {
			logger.debug("VOLTS;" + row[2] + ";PERCENT;" + row[4] + ";HASPOWER;" + row[6]);
			try {
				voltage = Float.valueOf(row[2]);
				charge = Float.valueOf(row[4]);
			} catch (NumberFormatException e) {
				setErrorCode(RESPONSE_LENGTH_ERROR);
				setStatus(STATUS_ERROR);
				return;
			}
			switch (row[6]) {
			case RESPONSE_TRUE:
				hasPower = true;
				break;
			case RESPONSE_FALSE:
				hasPower = true;
				break;
			default:
				setErrorCode(RESPONSE_LENGTH_ERROR);
				setStatus(STATUS_ERROR);
				return;
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
		dataOutputStream.write("READBATTERY\r\n".getBytes());
		dataOutputStream.flush();
	}

	@Override
	public void parseCommandRow(String[] row) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendResponse(OutputStream dataOutputStream) throws IOException {
		String payload = "READBATTERY;VOLTS;" + voltage + ";PERCENT;" + charge + ";HASPOWER;" + hasPower + "\r\n";
		dataOutputStream.write(payload.getBytes());
		dataOutputStream.flush();
	}

	public Float getVoltage() {
		return voltage;
	}

	public Float getCharge() {
		return charge;
	}

	public boolean isHasPower() {
		return hasPower;
	}

	public void setVoltage(Float voltage) {
		this.voltage = voltage;
	}

	public void setCharge(Float charge) {
		this.charge = charge;
	}

	public void setHasPower(boolean hasPower) {
		this.hasPower = hasPower;
	}

}

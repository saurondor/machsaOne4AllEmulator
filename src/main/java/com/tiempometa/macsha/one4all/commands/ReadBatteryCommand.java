/*
 * Copyright (c) 2019 Gerardo Esteban Tasistro Giubetic
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 */
package com.tiempometa.macsha.one4all.commands;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 *
 */
public class ReadBatteryCommand extends MacshaCommand {

	private static final Logger logger = LogManager.getLogger(ReadBatteryCommand.class);

	// Con el fin de recibir el estado de la bater?a interna, el host env?a
	// READBATTERY;<CrLf>.
	//
	// El One4All responde:
	// READBATTERY;VOLTS<Volts>;PERCENT<Percent>;HASPOWER<HasPower><CrLf>
	//
	// Donde:
	// <Volts>, es el voltaje actual de la bater?a. 26,5 Volts es el valor m?ximo.
	// <Percent>, es la carga actual en porcentaje de la bater?a. Desde 0% hasta
	// 100%.
	// <HasPower>, es:
	// true, si el sistema esta conectado a la red el?ctrica y cargando.
	// false, si el sistema no esta conectado a la red el?ctrica.
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

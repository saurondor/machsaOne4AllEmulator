/**
 * 
 */
package com.tiempometa.macsha.one4all.commands;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author gtasi
 *
 */
public class GetStatusCommand extends MacshaCommand {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tiempometa.macsha.one4all.commands.MacshaCommand#parseCommandRow(java.
	 * lang.String[])
	 */

	private Integer deviceId;
	private Integer ipAddress;
	private Float batteryVoltage;
	private Float batteryLevel;
	private boolean hasAcPower;
	private String model = "One4All 5";
	private String serialNumber = "O4A5-00051";
	private String logFile = "20191117-131717.csv";
	private boolean antenna1 = true;
	private boolean antenna2 = true;
	private boolean antenna3 = true;
	private boolean antenna4 = true;
	private boolean antenna5 = true;
	private boolean antenna6 = true;
	private boolean antenna7 = true;
	private boolean antenna8 = true;

	@Override
	public void parseCommandRow(String[] row) {
		if (row.length > 1) {
			switch (row[1]) {
			case "system":
				setStatus(STATUS_OK);
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
	 * com.tiempometa.macsha.one4all.commands.MacshaCommand#parseResponseRow(java.
	 * lang.String[])
	 */
	@Override
	public void parseResponseRow(String[] row) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tiempometa.macsha.one4all.commands.MacshaCommand#sendCommand(java.io.
	 * OutputStream)
	 */
	@Override
	public void sendCommand(OutputStream dataOutputStream) throws IOException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tiempometa.macsha.one4all.commands.MacshaCommand#sendResponse(java.io.
	 * OutputStream)
	 */
	@Override
	public void sendResponse(OutputStream dataOutputStream) throws IOException {
		String acPower = "False";
		if (isHasAcPower()) {
			acPower = "True";
		}
//		String response = "GETSTATUS;3;192.168.1.13;O4A5-00051;One4All 5;1234678;Macsha Mexico;MC;46.2;True;20191117-131717.csv;0;True;100;26.5;True;False;False;True;America/Mexico_City;False;never;960122610340BFDD;Motorola;OK;fcc;5;3;False;1.1.3.0;False;False;False;False;False;False;3;3.0;15336649CD289D56E1B2;False;;0;False";
		String response = "GETSTATUS;" + deviceId + ";" + ipAddress + ";" + serialNumber + ";" + model + ";"
				+ activeAntennas() + ";Macsha Mexico;MC;46.2;True;" + logFile + ";0;" + acPower + ";"
				+ batteryLevel.intValue() + ";" + batteryVoltage
				+ ";True;False;False;True;America/Mexico_City;False;never;960122610340BFDD;Motorola;OK;fcc;5;3;False;1.1.3.0;False;False;False;False;False;False;3;3.0;15336649CD289D56E1B2;False;;0;False";
		dataOutputStream.write(response.getBytes());
		dataOutputStream.flush();
	}

	private String activeAntennas() {
		StringBuffer antennaString = new StringBuffer();
		if (isAntenna1()) {
			antennaString.append("1");
		}
		if (isAntenna2()) {
			antennaString.append("2");
		}
		if (isAntenna3()) {
			antennaString.append("3");
		}
		if (isAntenna4()) {
			antennaString.append("4");
		}
		if (isAntenna5()) {
			antennaString.append("5");
		}
		if (isAntenna6()) {
			antennaString.append("6");
		}
		if (isAntenna7()) {
			antennaString.append("7");
		}
		if (isAntenna8()) {
			antennaString.append("8");
		}
		return antennaString.toString();
	}

	public Integer getDeviceId() {
		return deviceId;
	}

	public Integer getIpAddress() {
		return ipAddress;
	}

	public Float getBatteryVoltage() {
		return batteryVoltage;
	}

	public Float getBatteryLevel() {
		return batteryLevel;
	}

	public String getModel() {
		return model;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public String getLogFile() {
		return logFile;
	}

	public boolean isAntenna1() {
		return antenna1;
	}

	public boolean isAntenna2() {
		return antenna2;
	}

	public boolean isAntenna3() {
		return antenna3;
	}

	public boolean isAntenna4() {
		return antenna4;
	}

	public boolean isAntenna5() {
		return antenna5;
	}

	public boolean isAntenna6() {
		return antenna6;
	}

	public boolean isAntenna7() {
		return antenna7;
	}

	public boolean isAntenna8() {
		return antenna8;
	}

	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}

	public void setIpAddress(Integer ipAddress) {
		this.ipAddress = ipAddress;
	}

	public void setBatteryVoltage(Float batteryVoltage) {
		this.batteryVoltage = batteryVoltage;
	}

	public void setBatteryLevel(Float batteryLevel) {
		this.batteryLevel = batteryLevel;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}

	public void setAntenna1(boolean antenna1) {
		this.antenna1 = antenna1;
	}

	public void setAntenna2(boolean antenna2) {
		this.antenna2 = antenna2;
	}

	public void setAntenna3(boolean antenna3) {
		this.antenna3 = antenna3;
	}

	public void setAntenna4(boolean antenna4) {
		this.antenna4 = antenna4;
	}

	public void setAntenna5(boolean antenna5) {
		this.antenna5 = antenna5;
	}

	public void setAntenna6(boolean antenna6) {
		this.antenna6 = antenna6;
	}

	public void setAntenna7(boolean antenna7) {
		this.antenna7 = antenna7;
	}

	public void setAntenna8(boolean antenna8) {
		this.antenna8 = antenna8;
	}

	public boolean isHasAcPower() {
		return hasAcPower;
	}

	public void setHasAcPower(boolean hasAcPower) {
		this.hasAcPower = hasAcPower;
	}

}

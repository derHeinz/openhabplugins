package org.openhab.binding.superlegend.internal;

/**
 * Struct to identify a device.
 * @author Heinz
 *
 */
public class SuperlegendDevice {
	
	private String ipAddress;
	
	private String macAddress; // only store uppercase mac in here.
	
	private String type;
	
	public SuperlegendDevice(String anIpAddress, String aMacAddress, String aType) {
		ipAddress = anIpAddress;
		macAddress = aMacAddress;
		type = aType;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return "Superlegend device, IP: " + ipAddress + ", mac: " + macAddress + ", type: " + type;
	}

}

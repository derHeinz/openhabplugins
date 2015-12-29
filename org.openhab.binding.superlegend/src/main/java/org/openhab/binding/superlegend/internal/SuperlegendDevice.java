package org.openhab.binding.superlegend.internal;

/**
 * Struct to identify a device.
 * @author Heinz
 *
 */
public class SuperlegendDevice {
	
	private String ipAddress;
	
	private String macAddress;
	
	private String type;
	
	public SuperlegendDevice(String anIpAddress, String aMacAddress, String aType) {
		ipAddress = anIpAddress;
		macAddress = aMacAddress;
		type = aType;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return "Superlegend device, IP: " + ipAddress + ", mac: " + macAddress + ", type: " + type;
	}

}

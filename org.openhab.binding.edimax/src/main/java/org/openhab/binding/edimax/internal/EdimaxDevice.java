package org.openhab.binding.edimax.internal;

public class EdimaxDevice {
	public String ip;
	public String mac;

	public EdimaxDevice() {
	}

	public String getIp() {
		return ip;
	}

	public String getMac() {
		return mac;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

}
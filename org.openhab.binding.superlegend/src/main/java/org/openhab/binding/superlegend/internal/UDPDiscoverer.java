package org.openhab.binding.superlegend.internal;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Discovers Superlegend devices.
 * 
 * @author Heinz
 * 
 */
public class UDPDiscoverer {

	private InetAddress broadcastAddress;

	/**
	 * Initialize discovery.
	 * 
	 * @throws SocketException
	 * @throws UnknownHostException
	 */
	protected void initialize() throws SocketException, UnknownHostException {
		broadcastAddress = InetAddress.getByName("255.255.255.255");
	}

	private boolean isInitialized() {
		return (broadcastAddress != null ? true : false);
	}

	/**
	 * Receive discovery information.
	 * 
	 * @return
	 * @throws IOException
	 */
	protected synchronized String[] receiveDiscovery() throws IOException {
		List<String> result = new ArrayList<>();
		// construct paket
		String sentence = "HF-A11ASSISTHREAD";
		byte[] sendData = sentence.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcastAddress, 48899);
		// find return
		DatagramSocket clientSocket = null;
		try {
			clientSocket = new DatagramSocket(12345); // choose a random port,
														// because with an empty
														// constructor sometimes
														// a port is choosen
														// that is already in
														// use (BindException
														// raised somehow).
			clientSocket.setReuseAddress(true);
			clientSocket.send(sendPacket);

			// construct receiver
			byte[] receiveData = new byte[1024];

			// while (clientSocket.)
			clientSocket.setSoTimeout(1000 * 5);
			try {
				while (true) {
					DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
					clientSocket.receive(receivePacket);
					result.add(new String(receivePacket.getData()));
				}
			} catch (SocketTimeoutException e) {
				// intended to happen
			}
		} finally {
			if (clientSocket != null) {
				clientSocket.close();
			}
		}

		return result.toArray(new String[result.size()]);
	}

	/**
	 * Parse the device from the string.
	 * 
	 * @param aReceivedData
	 * @return
	 */
	private SuperlegendDevice parseDevice(String aReceivedData) {
		if ((aReceivedData == null) || (aReceivedData.length() < 1)) {
			return null;
		}

		String[] split = aReceivedData.split(",");
		return new SuperlegendDevice(split[0], split[1].toUpperCase(), split[2]);
	}

	public SuperlegendDevice[] discoverDevices() throws IOException {
		SuperlegendDevice[] result;
		if (!isInitialized()) {
			initialize();
		}
		// discover devices
		String[] discoveredDevices = receiveDiscovery();
		// parse strings to devices
		result = new SuperlegendDevice[discoveredDevices.length];
		for (int i = 0; i < discoveredDevices.length; i++) {
			String received = discoveredDevices[i];
			result[i] = parseDevice(received);
		}
		return result;
	}

	public static void main(String[] args) throws IOException {
		UDPDiscoverer discoverer = new UDPDiscoverer();
		for (SuperlegendDevice dev : discoverer.discoverDevices()) {
			logToConsole(dev);
		}
	}

	public static void main2(String args[]) throws Exception {
		UDPDiscoverer discoverer = new UDPDiscoverer();
		UDPDiscoverer discoverer2 = new UDPDiscoverer();

		for (int i = 0; i < 1000; i++) {
			System.out.println("Running discovery.");
			try {
				doDiscover(discoverer);
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				doDiscover(discoverer2);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Thread.sleep(1000);
		}
	}

	private static void doDiscover(UDPDiscoverer discoverer) throws IOException {
		SuperlegendDevice[] discoverDevices = discoverer.discoverDevices();
		for (SuperlegendDevice device : discoverDevices) {
			logToConsole(device);
		}
	}

	private static void logToConsole(SuperlegendDevice device) {
		System.out.println("Device found. IP:" + device.getIpAddress() + " MAC:" + device.getMacAddress());
	}

}

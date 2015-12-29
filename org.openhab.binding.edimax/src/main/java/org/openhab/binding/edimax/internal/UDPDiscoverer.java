package org.openhab.binding.edimax.internal;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Discovers EDIMAX devices by using the build-in UDP discovery. One sends a UDP
 * packete with code 1300 and the device responds with code 1400 if it is there.
 * 
 * @author Heinz
 * 
 */
public class UDPDiscoverer implements Discoverer {

	private static final String part1 = "<param><code value=\"1300\" /><port value=\"";
	private static final String part2 = "\" /></param>";

	private static final String answer_start = "<param><code value=\"1400\"";

	/**
	 * Static port, as no other port seems to work with my device.
	 */
	private static final int PORT = 50474;

	private List<String> discover() throws SocketException, UnknownHostException, IOException {
		List<String> discoveredDevices = new ArrayList<>();
		DatagramSocket serverSocket = null;
		try {
			serverSocket = new DatagramSocket(PORT);

			// send UDP broadcast
			InetAddress ipAddress = InetAddress.getByName("255.255.255.255");
			byte[] sendData = (part1 + PORT + part2).getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, PORT);
			serverSocket.send(sendPacket);

			// receive
			serverSocket.setSoTimeout(1000);
			byte[] receiveData = new byte[1024];

			try {
				while (true) {
					DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

					serverSocket.receive(receivePacket);
					String sentence = new String(receivePacket.getData());

					if (!StringUtils.isEmpty(sentence) && sentence.startsWith(answer_start)) {
						InetAddress discoveredIp = receivePacket.getAddress();
						discoveredDevices.add(discoveredIp.getHostAddress());
					}

				}
			} catch (SocketTimeoutException e) {
				// intended to happen
			}
		} finally {
			if (serverSocket != null) {
				serverSocket.close();
			}
		}
		return discoveredDevices;
	}

	@Override
	public EdimaxDevice[] discoverDevices() throws DiscoveryException {
		List<EdimaxDevice> result = new ArrayList<>();
		List<String> discoveredIPs;
		try {
			discoveredIPs = discover();
			for (String ip : discoveredIPs) {
				String mac = HTTPSend.getMAC(ip);
				EdimaxDevice dev = new EdimaxDevice();
				dev.setIp(ip);
				dev.setMac(mac);
				result.add(dev);
			}

		} catch (IOException e) {
			throw new DiscoveryException(e);
		}
		return result.toArray(new EdimaxDevice[result.size()]);
	}

	public static void main(String[] args) throws DiscoveryException {
		UDPDiscoverer udpDiscoverer = new UDPDiscoverer();
		for (EdimaxDevice dev : udpDiscoverer.discoverDevices()) {
			System.out.println("IP: " + dev.getIp() + ", mac:" + dev.getMac());
		}

	}

}

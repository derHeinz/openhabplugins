package org.openhab.binding.edimax.internal;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Simply discovers edimax SP-2101 devices by checking through all devices. TODO
 * does only work if the default password is used on the device.
 * 
 * @author Heinz
 * 
 */
public class PlainDiscoverer implements Discoverer {

	/**
	 * IP prefix for search.
	 */
	private final String ipPrefix;

	/**
	 * Start postfix. Should be < than end Postfix.
	 */
	private final int startIpPostfix;

	/**
	 * End postfix. SHould be > than start postfix.
	 */
	private final int endIpPostfix;

	/**
	 * IP Prefix configurable.
	 * 
	 * @param aPrefix
	 */
	public PlainDiscoverer(String aPrefix, int startPost, int endPost) {
		ipPrefix = aPrefix;
		startIpPostfix = startPost;
		endIpPostfix = endPost;
	}

	public class PortScanUsage {
		private String ip;
		private int port;
		private Boolean result;

		public String getIp() {
			return ip;
		}

		public int getPort() {
			return port;
		}

		public Boolean getResult() {
			return result;
		}
	}

	private Future<PortScanUsage> portIsOpen(final ExecutorService es, final String ip, final int port,
			final int timeout) {
		return es.submit(new Callable<PortScanUsage>() {
			@Override
			public PortScanUsage call() {
				PortScanUsage result = new PortScanUsage();
				result.ip = ip;
				result.port = port;
				try {
					Socket socket = new Socket();
					socket.connect(new InetSocketAddress(ip, port), timeout);
					socket.close();
					result.result = true;
				} catch (Exception ex) {
					result.result = false;
				}
				return result;
			}
		});
	}

	private EdimaxDevice[] discoverDevicesInternal() throws InterruptedException, ExecutionException {
		final ExecutorService es = Executors.newFixedThreadPool(10);

		final List<Future<PortScanUsage>> futures = new ArrayList<>();
		for (int last = startIpPostfix; last <= endIpPostfix; last++) {
			futures.add(portIsOpen(es, ipPrefix + last, HTTPSend.PORT, 200));
		}
		es.shutdown();

		List<EdimaxDevice> discovered = new ArrayList<>();

		for (final Future<PortScanUsage> f : futures) {
			PortScanUsage portScanUsage = f.get();
			if (portScanUsage.result) {
				String mac;
				try {
					mac = new HTTPSend().getMAC(portScanUsage.ip).toUpperCase();
					if (mac != null) {
						// found a device!
						EdimaxDevice d = new EdimaxDevice();
						d.setIp(portScanUsage.getIp());
						d.setMac(mac);
						discovered.add(d);
					}
				} catch (IOException e) {
					// ignore - this is not a correct device.
				}
			}
		}
		return discovered.toArray(new EdimaxDevice[discovered.size()]);
	}

	@Override
	public EdimaxDevice[] discoverDevices() throws DiscoveryException {
		try {
			return discoverDevicesInternal();
		} catch (InterruptedException | ExecutionException e) {
			throw new DiscoveryException(e);
		}
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		PlainDiscoverer discoverer = new PlainDiscoverer("192.168.2.", 100, 120);
		for (EdimaxDevice d : discoverer.discoverDevicesInternal()) {
			System.out.println("found ip:" + d.getIp() + " mac:" + d.getMac());
		}
	}

}

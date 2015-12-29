package org.openhab.binding.edimax.internal;

/**
 * Discover interface.
 * @author Heinz
 *
 */
public interface Discoverer {
	
	/**
	 * Find devices.
	 * @return
	 * @throws DiscoveryException
	 */
	public EdimaxDevice[] discoverDevices() throws DiscoveryException;

}

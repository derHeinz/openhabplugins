package org.openhab.binding.edimax.internal;

/**
 * Problem occured while discovering devices.
 * @author Heinz
 *
 */
public class DiscoveryException extends Exception {

	/**
	 * .
	 */
	private static final long serialVersionUID = -2990047766796813449L;

	public DiscoveryException(Throwable cause) {
		super(cause);
	}

}

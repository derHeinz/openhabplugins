package org.openhab.binding.superlegend.internal;

import java.awt.Color;
import java.io.IOException;

import org.openhab.binding.superlegend.SuperlegendBindingProvider;
import org.openhab.core.binding.AbstractActiveBinding;
import org.openhab.core.library.types.HSBType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuperlegendBinding extends AbstractActiveBinding<SuperlegendBindingProvider> {

	private static final Logger logger = LoggerFactory.getLogger(SuperlegendBinding.class);

	/**
	 * Discovers superlegend devices.
	 */
	private UDPDiscoverer discoverer = new UDPDiscoverer();

	/**
	 * Real devices discovered.
	 */
	private SuperlegendDevice[] discoveredDevices;

	/**
	 * Commander to send infos to devices.
	 */
	private SuperlegendCommandSender commander = new SuperlegendCommandSender();

	/**
	 * Discover step counter.
	 */
	private int discoverStep = 0;

	/**
	 * The amount of executes() which are skipped until another discover
	 * happens. @See #getRefreshInterval.
	 */
	private static final int DISCOVER_SKIP_STEP = 60;

	/**
	 * Checks whether to discover or not (not always discover when thread runs).
	 * 
	 * @return
	 */
	protected boolean shouldDiscover() {
		boolean doDiscover = false;
		if (discoverStep % DISCOVER_SKIP_STEP == 0) {
			doDiscover = true;
			discoverStep = 0; // under- overrun
		}

		discoverStep++;
		return doDiscover;
	}

	/**
	 * How many exception occur until it is considered to be a real error.
	 * Should be used in conjunction with {@link #getRefreshInterval()}.
	 */
	private static final int EXCEPTION_COUNT_TO_REAL_ERROR = 4;

	/**
	 * How many errors occured in succession.
	 */
	private int errorCount;

	@Override
	protected void execute() {
		
		// discover
		if (shouldDiscover()) {
			discover();
		}

		// find devices current color -> and post
		for (SuperlegendBindingProvider provider : providers) {
			for (String itemName : provider.getItemNames()) {
				SuperlegendBindingConfiguration config = ((SuperlegendGenericBindingProvider) provider)
						.getConfig(itemName);
				SuperlegendDevice device = findRealDevice(config);
				if (device == null) {
					notFoundLogMessage(itemName);
					continue;
				}

				// find current state of all configured devices and set
				SuperlegendDeviceState devInfo;
				try {
					devInfo = commander.getInformation(device.getIpAddress());
					State newState;
					if (!devInfo.isOn()) {
						newState = OnOffType.OFF;
					} else {
						newState = new HSBType(devInfo.getColor());
					}
					eventPublisher.postUpdate(itemName, newState);
				} catch (IOException e) {
					logger.error("Unable to retrieve information for device " + device.getIpAddress() + ".", e);
				}

			}
		}

	}

	protected void discover() {
		SuperlegendDevice[] discovered = null;
		try {
			discovered = discoverer.discoverDevices();
			// no error present - all fine
			errorCount = 0;
			discoveredDevices = discovered;
		} catch (IOException e) {
			// socket timeout may occur while somebody else discovers the
			// device.
			errorCount++;
			if (errorCount >= EXCEPTION_COUNT_TO_REAL_ERROR) {
				// real error occured - set current devices to those
				discoveredDevices = discovered;
				logger.error("Error discovering Superlegend devices. Amount of exceptions: "
						+ EXCEPTION_COUNT_TO_REAL_ERROR, e);
			} else {
				logger.debug("Interim error discovering Superlegend devices.", e);
			}
		}
	}

	@Override
	public void activate() {
		super.activate();
		// do the active stuff
		setProperlyConfigured(true);
	}

	@Override
	protected void internalReceiveCommand(String itemName, Command command) {
		for (SuperlegendBindingProvider provider : providers) {
			SuperlegendBindingConfiguration config = ((SuperlegendGenericBindingProvider) provider).getConfig(itemName);
			SuperlegendDevice device = findRealDevice(config);
			if (device == null) {
				notFoundLogMessage(itemName);
				continue;
			}
			changeValue(device, command);
		}
	}

	protected void notFoundLogMessage(String itemName) {
		logger.debug("No real device for item: " + itemName + " found.");
	}

	private void changeValue(SuperlegendDevice device, Command command) {
		String deviceIp = device.getIpAddress();

		
		try {
			if (command instanceof OnOffType) {
				if (OnOffType.OFF.equals(command)) {
					// switch off if it's on
					if (commander.getInformation(deviceIp).isOn()) {
						commander.switchOff(deviceIp);
					}
				} else if (OnOffType.ON.equals(command)) {
					// switch on if it's off
					if (!commander.getInformation(deviceIp).isOn()) {
						commander.switchOn(deviceIp);
					}
				}
			} else if (command instanceof HSBType) {
				Color colorToBeSet = ((HSBType) command).toColor();
				SuperlegendDeviceState information = commander.getInformation(deviceIp);

				if (!information.getColor().equals(colorToBeSet)) {
					commander.switchColor(deviceIp, colorToBeSet);
				}
				if (!information.isOn()) {
					commander.switchOn(deviceIp);
				}
			} else {
				logger.error("Unsupported type.");
			}
		} catch (IOException e) {
			logger.error("Communication error.", e);
		}
	}

	/**
	 * Find suitable device previously discovered.
	 * 
	 * @param aConfig
	 * @return
	 */
	private SuperlegendDevice findRealDevice(SuperlegendBindingConfiguration aConfig) {
		if (discoveredDevices == null) {
			return null;
		}
		for (SuperlegendDevice device : discoveredDevices) {
			String mac = aConfig.getMacAddress().toUpperCase();
			// check if MAC matches
			if (mac.equals(device.getMacAddress())) {
				return device;
			}
		}
		return null;
	}

	@Override
	protected long getRefreshInterval() {
		// once every 30 seconds.
		return 1000 * 30;
	}

	@Override
	protected String getName() {
		return "Superlegend discovery thread";
	}

}

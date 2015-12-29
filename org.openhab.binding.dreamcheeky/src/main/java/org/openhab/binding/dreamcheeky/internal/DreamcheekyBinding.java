package org.openhab.binding.dreamcheeky.internal;

import java.awt.Color;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import ledmessageboard.FontMessageBoardDriver;

import org.openhab.binding.dreamcheeky.DreamcheekyBindingProvider;
import org.openhab.binding.dreamcheeky.internal.DreamcheekyBindingConfiguration.MAILNOTIFIER_OPTION;
import org.openhab.binding.dreamcheeky.internal.DreamcheekyBindingConfiguration.TYPE;
import org.openhab.binding.dreamcheeky.internal.mailnotifier.MailNotifierColor;
import org.openhab.binding.dreamcheeky.internal.mailnotifier.NativeMailNotifierDriver;
import org.openhab.core.binding.AbstractBinding;
import org.openhab.core.binding.BindingProvider;
import org.openhab.core.library.types.HSBType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DreamcheekyBinding extends
		AbstractBinding<DreamcheekyGenericBindingProvider> {

	private static final Logger logger = LoggerFactory
			.getLogger(DreamcheekyBinding.class);

	@Override
	public void activate() {
		logger.debug("Activate");
		messageBoard = new FontMessageBoardDriver();
		// connectors to real devices will be started on first use.
	}

	@Override
	public void deactivate() {
		logger.debug("Deactivate");
		NativeMailNotifierDriver.theInstance().closeMailNotifierConnection();
		stopLEDMessageBoard();
	}

	@Override
	protected void internalReceiveCommand(String itemName, Command command) {
		changeValue(itemName, command);
	}

	@Override
	protected void internalReceiveUpdate(String itemName, State newState) {
		changeValue(itemName, newState);
	}

	private void changeValue(String itemName, Object commandOrState) {

		for (DreamcheekyGenericBindingProvider provider : providers) {
			DreamcheekyBindingConfiguration config = provider
					.getConfig(itemName);

			if (config.getType() == TYPE.MAILNOTIFIER) {
				changeMailNotifierColor(config, itemName, commandOrState);
			} else if (config.getType() == TYPE.LEDMESSAGEBOARD) {
				displayOnLEDMessageBoard(config, itemName, commandOrState);
			}

		}
	}

	/**
	 * Binding changed stuff.
	 */

	@Override
	public void allBindingsChanged(BindingProvider provider) {
		deactivateDeviceCommunicationIfNotNeeded();
	}

	@Override
	public void bindingChanged(BindingProvider provider, String itemName) {
		deactivateDeviceCommunicationIfNotNeeded();
	}

	private void deactivateDeviceCommunicationIfNotNeeded() {
		boolean mailNotifierBindingActive = false;
		boolean messageBoardBindingActive = false;
		for (DreamcheekyBindingProvider prov : providers) {
			if (prov.containsBindingForMailNotifier()) {
				mailNotifierBindingActive = true;
			}
			if (prov.containsBindingForMessageBoard()) {
				messageBoardBindingActive = true;
			}
		}

		if (!mailNotifierBindingActive) {
			NativeMailNotifierDriver.theInstance()
					.closeMailNotifierConnection();
			mailNotifierColorCache.clear();
		}

		if (!messageBoardBindingActive) {
			stopLEDMessageBoard();
			messageBoardCache.clear();
		}
	}

	/**
	 * LED Message Board related things.
	 */

	/**
	 * Message board driver.
	 */
	private FontMessageBoardDriver messageBoard;

	private Map<String, String> messageBoardCache = new HashMap<String, String>();

	private void stopLEDMessageBoard() {
		messageBoard.stop();
	}

	private void displayOnLEDMessageBoard(
			DreamcheekyBindingConfiguration config, String itemName,
			Object commandOrState) {
		if (commandOrState instanceof StringType) {
			StringType string = (StringType) commandOrState;
			String oldValue = messageBoardCache.get(itemName);

			// if this is already displayed (cached) - don't refresh
			if ((oldValue != null) && oldValue.equals(string.toString())
					&& messageBoard.isRunning()) {
				return;
			}

			messageBoard.show(string.toString(),
					config.getLedmessageBoardStrategy());
			messageBoardCache.put(itemName, string.toString());

		}

	}

	/**
	 * Mail notifier related things.
	 */

	/**
	 * Caches previous colors for the mailnotifier values in order to provide
	 * smooth changes.
	 */
	private Map<String, MailNotifierColor> mailNotifierColorCache = new HashMap<String, MailNotifierColor>();

	private void changeMailNotifierColor(
			DreamcheekyBindingConfiguration config, String itemName,
			Object commandOrState) {
		MailNotifierColor newColor = null;
		if (commandOrState instanceof OnOffType) {
			if (OnOffType.ON.equals(commandOrState)) {
				newColor = MailNotifierColor.LIGHT_GREY;
			} else if (OnOffType.OFF.equals(commandOrState)) {
				newColor = MailNotifierColor.NO_COLOR;
			}
		} else if (commandOrState instanceof HSBType) {
			Color color = ((HSBType) commandOrState).toColor();
			newColor = new MailNotifierColor(
					colorToMailNotifierColorValue(color.getRed()),
					colorToMailNotifierColorValue(color.getGreen()),
					colorToMailNotifierColorValue(color.getBlue()));
		}

		if (newColor != null) {

			try {
				// check how to update
				if (config.getMnOption() == MAILNOTIFIER_OPTION.SMOOTH) {
					MailNotifierColor oldColor = mailNotifierColorCache
							.get(itemName);
					if (oldColor == null) {
						oldColor = MailNotifierColor.NO_COLOR;
					}
					NativeMailNotifierDriver.theInstance().displayGradient(
							oldColor, newColor);
				} else if (config.getMnOption() == MAILNOTIFIER_OPTION.STRAIGHT) {
					NativeMailNotifierDriver.theInstance()
							.switchColor(newColor);
				}
				// put color in cache for next update
				mailNotifierColorCache.put(itemName, newColor);
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("Error sending command to mailnotifier.", e);
			}
		}
	}

	/**
	 * Unlike java.awt.Color which is 0-255, MailNotifier is only 0-64.
	 */
	private int colorToMailNotifierColorValue(int aColorValue) {
		BigDecimal number = new BigDecimal(aColorValue).multiply(
				BigDecimal.valueOf(64)).divide(BigDecimal.valueOf(255), 2,
				BigDecimal.ROUND_HALF_UP);
		return number.intValue();
	}

}

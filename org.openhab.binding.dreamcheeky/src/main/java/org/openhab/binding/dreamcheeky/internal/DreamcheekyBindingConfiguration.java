package org.openhab.binding.dreamcheeky.internal;

import ledmessageboard.strategy.DisplayStrategy;
import ledmessageboard.strategy.ScrollingDisplayStrategy;

import org.openhab.core.binding.BindingConfig;
import org.openhab.core.items.Item;
import org.openhab.core.library.items.ColorItem;
import org.openhab.core.library.items.StringItem;
import org.openhab.core.library.items.SwitchItem;
import org.openhab.model.item.binding.BindingConfigParseException;

public class DreamcheekyBindingConfiguration implements BindingConfig {

	/**
	 * Parsing iterals.
	 */
	enum TYPE {
		MAILNOTIFIER("mailnotifier"),

		LEDMESSAGEBOARD("ledmessageboard");

		private final String representation;

		TYPE(String representation) {
			this.representation = representation;
		}

		public String getRepresentation() {
			return representation;
		}
	}

	enum MAILNOTIFIER_OPTION {
		SMOOTH("smooth"), STRAIGHT("straight");

		private final String representation;

		MAILNOTIFIER_OPTION(String representation) {
			this.representation = representation;
		}

		public String getRepresentation() {
			return representation;
		}
	}

	/**
	 * General type of the item.
	 */
	private TYPE type;

	/**
	 * Option for a binding with the mail notifier.
	 */
	private MAILNOTIFIER_OPTION mnOption;
	
	/**
	 * Options for the message board display strategy.
	 */
	private DisplayStrategy ledmessageBoardStrategy;

	public DreamcheekyBindingConfiguration() {
		// const
	}

	public TYPE getType() {
		return type;
	}
	
	public DisplayStrategy getLedmessageBoardStrategy() {
		return ledmessageBoardStrategy;
	}


	public MAILNOTIFIER_OPTION getMnOption() {
		return mnOption;
	}

	public void parse(Item item, String bindingConfig)
			throws BindingConfigParseException {
		// 2 typen: mailnotifier, ledmessageboard
		// a) evtl. noch usb things configurable (product & vendor id)
		// b) evtl noch index oder so um mehrere der selben geraete voneinander
		// zu unterscheeden -> kann ich nicht testen habe ich nicht.
		// mailnotifier: SwitchItem, ColorItem
		// ledmessageboard: StringItem evtl.: DateTime-, Number- & LocationItem

		if (bindingConfig == null || "".equals(bindingConfig)) {

			throw new BindingConfigParseException("No type specified in \""
					+ bindingConfig + "\". Please use one of " + typeString()
					+ ".");
		}

		if (bindingConfig.startsWith(TYPE.MAILNOTIFIER.getRepresentation())) {
			type = TYPE.MAILNOTIFIER;
			// check item type
			if (!(item instanceof SwitchItem || item instanceof ColorItem)) {
				throw new BindingConfigParseException("item '" + item.getName()
						+ "' is of type '" + item.getClass().getSimpleName()
						+ "', only Switch- and ColorItems are allowed in "
						+ TYPE.MAILNOTIFIER.getRepresentation()
						+ " - please check your *.items configuration");
			}

			// check options if available
			if (bindingConfig.contains(":")) {

				String[] split = bindingConfig.split(":");
				if (split.length != 2
						|| (countOccurences(bindingConfig, ":") != 1)) {
					throw new BindingConfigParseException(
							"Too many options given by \"" + bindingConfig
									+ "\".");
				}
				// check valid options
				if (split[1].equals(MAILNOTIFIER_OPTION.SMOOTH
						.getRepresentation())) {
					mnOption = MAILNOTIFIER_OPTION.SMOOTH;
				} else if (split[1].equals(MAILNOTIFIER_OPTION.STRAIGHT
						.getRepresentation())) {
					mnOption = MAILNOTIFIER_OPTION.STRAIGHT;
				} else {
					throw new BindingConfigParseException("Invalid option for "
							+ TYPE.MAILNOTIFIER.getRepresentation()
							+ " type. Please use one of " + mnOptionsString()
							+ ".");
				}
			} else {
				mnOption = MAILNOTIFIER_OPTION.STRAIGHT; // default option is
															// straight.
				// check no other rubbish is attached
				if (bindingConfig.length() != TYPE.MAILNOTIFIER
						.getRepresentation().length()) {
					throw new BindingConfigParseException(
							"Invalid further configuration for "
									+ TYPE.MAILNOTIFIER.getRepresentation()
									+ " type.");
				}
			}

		} else if (bindingConfig.startsWith(TYPE.LEDMESSAGEBOARD
				.getRepresentation())) {
			
			if (!(item instanceof StringItem)) {
				throw new BindingConfigParseException("item '" + item.getName()
						+ "' is of type '" + item.getClass().getSimpleName()
						+ "', only StringItems are allowed in "
						+ TYPE.LEDMESSAGEBOARD.getRepresentation()
						+ " - please check your *.items configuration");
			}
			
			type = TYPE.LEDMESSAGEBOARD;
			
			int refreshinterval = 100;
			if (bindingConfig.contains(":")) {
				String[] split = bindingConfig.split(":");
				if (split.length != 2) {
					throw new BindingConfigParseException(
							"Too many options given by \"" + bindingConfig
									+ "\".");
				}
				try {
					refreshinterval = Integer.parseInt(split[1]);
				} catch (NumberFormatException e) {
					throw new BindingConfigParseException(
							"Interval \"" + split[1] + " is not a number. Full binding string is: " + bindingConfig
									+ "\".");
				}
				
			}
			
			// TODO maybe allow configuration of the strategy here
			ledmessageBoardStrategy = new ScrollingDisplayStrategy(refreshinterval, true, true);
		} else {

			// show what types are available
			throw new BindingConfigParseException(
					"No suitable type specified in \"" + bindingConfig
							+ "\". Please use one of " + typeString() + ".");
		}
	}

	protected static String typeString() {
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < TYPE.values().length; i++) {
			TYPE type = TYPE.values()[i];
			buff.append(type.getRepresentation());
			if (i <= TYPE.values().length - 2) { // when it's not last append ,
				buff.append(",");
			}
		}
		return buff.toString();
	}

	protected static String mnOptionsString() {
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < MAILNOTIFIER_OPTION.values().length; i++) {
			MAILNOTIFIER_OPTION type = MAILNOTIFIER_OPTION.values()[i];
			buff.append(type.getRepresentation());
			if (i <= MAILNOTIFIER_OPTION.values().length - 2) { // when it's not
																// last append ,
				buff.append(",");
			}
		}
		return buff.toString();
	}

	protected static String arrayString(String[] arr) {
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < arr.length; i++) {
			buff.append(arr[i]);
			if (i <= arr.length - 2) { // when it's not last append ,
				buff.append(",");
			}
		}
		return buff.toString();
	}

	protected static int countOccurences(String input, String search) {
		int index = input.indexOf(search);
		int count = 0;
		while (index != -1) {
			count++;
			input = input.substring(index + 1);
			index = input.indexOf(search);
		}
		return count;
	}

}

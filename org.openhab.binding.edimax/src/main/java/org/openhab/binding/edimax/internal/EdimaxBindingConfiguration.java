package org.openhab.binding.edimax.internal;

import org.openhab.core.binding.BindingConfig;
import org.openhab.core.items.Item;
import org.openhab.model.item.binding.BindingConfigParseException;

public class EdimaxBindingConfiguration implements BindingConfig {

	enum TYPE {
		POWER, CURRENT, STATE
	}

	private String macAddress;

	private String password;

	private TYPE type;

	public void parse(Item item, String bindingConfig) throws BindingConfigParseException {
		if (bindingConfig == null || "".equals(bindingConfig)) {
			throw new BindingConfigParseException("No Configuration specified in \"" + bindingConfig + "\".");
		}

		String[] configParts = bindingConfig.split(":");
		if (configParts.length < 1) {
			throw new BindingConfigParseException(
					"Edimax configuration must contain macaddress. Optionally the password is to be specified, separated with ':'. And optionally it contains the type (separated from password by a ':'). Available types are: 'POWER', 'CURRENT' and 'STATE' which is default.");
		}

		macAddress = configParts[0];

		if (configParts.length > 1) {
			password = configParts[1];
		}
		if (configParts.length > 2) {
			type = TYPE.valueOf(configParts[2]);
		}
	}

	public String getMacAddress() {
		return macAddress;
	}

	public TYPE getType() {
		return type;
	}

	public String getPassword() {
		return password;
	}

}

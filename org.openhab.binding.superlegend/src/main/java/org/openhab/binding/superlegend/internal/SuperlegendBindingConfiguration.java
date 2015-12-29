package org.openhab.binding.superlegend.internal;

import org.openhab.core.binding.BindingConfig;
import org.openhab.core.items.Item;
import org.openhab.model.item.binding.BindingConfigParseException;

public class SuperlegendBindingConfiguration implements BindingConfig {

	private String macAddress;

	public void parse(Item item, String bindingConfig) throws BindingConfigParseException {
		if (bindingConfig == null || "".equals(bindingConfig)) {

			throw new BindingConfigParseException("No MAC Address specified in \"" + bindingConfig + "\".");
		}
		macAddress = bindingConfig;
	}

	public String getMacAddress() {
		return macAddress;
	}

}

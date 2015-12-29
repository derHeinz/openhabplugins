package org.openhab.binding.superlegend.internal;

import org.openhab.binding.superlegend.SuperlegendBindingProvider;
import org.openhab.core.items.Item;
import org.openhab.model.item.binding.AbstractGenericBindingProvider;
import org.openhab.model.item.binding.BindingConfigParseException;

public class SuperlegendGenericBindingProvider extends AbstractGenericBindingProvider implements
		SuperlegendBindingProvider {

	@Override
	public String getBindingType() {
		return "superlegend";
	}
	
	public SuperlegendBindingConfiguration getConfig(String itemName) {
		return (SuperlegendBindingConfiguration) bindingConfigs.get(itemName);
	}

	@Override
	public void validateItemType(Item item, String bindingConfig) throws BindingConfigParseException {
		new SuperlegendBindingConfiguration().parse(item, bindingConfig);
	}
	
	@Override
	public void processBindingConfiguration(String context, Item item,
			String bindingConfig) throws BindingConfigParseException {
		super.processBindingConfiguration(context, item, bindingConfig);
		SuperlegendBindingConfiguration config = new SuperlegendBindingConfiguration();
		config.parse(item, bindingConfig);
		addBindingConfig(item, config);
	}

}

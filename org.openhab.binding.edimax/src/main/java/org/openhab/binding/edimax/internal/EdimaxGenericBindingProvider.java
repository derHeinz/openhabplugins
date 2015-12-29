package org.openhab.binding.edimax.internal;

import org.openhab.binding.edimax.EdimaxBindingProvider;
import org.openhab.core.items.Item;
import org.openhab.model.item.binding.AbstractGenericBindingProvider;
import org.openhab.model.item.binding.BindingConfigParseException;

public class EdimaxGenericBindingProvider extends AbstractGenericBindingProvider implements
		EdimaxBindingProvider {

	@Override
	public String getBindingType() {
		return "edimax";
	}
	
	public EdimaxBindingConfiguration getConfig(String itemName) {
		return (EdimaxBindingConfiguration) bindingConfigs.get(itemName);
	}

	@Override
	public void validateItemType(Item item, String bindingConfig) throws BindingConfigParseException {
		new EdimaxBindingConfiguration().parse(item, bindingConfig);
	}
	
	@Override
	public void processBindingConfiguration(String context, Item item,
			String bindingConfig) throws BindingConfigParseException {
		super.processBindingConfiguration(context, item, bindingConfig);
		EdimaxBindingConfiguration config = new EdimaxBindingConfiguration();
		config.parse(item, bindingConfig);
		addBindingConfig(item, config);
	}

}

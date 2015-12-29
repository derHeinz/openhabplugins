package org.openhab.binding.dreamcheeky.internal;

import org.openhab.binding.dreamcheeky.DreamcheekyBindingProvider;
import org.openhab.binding.dreamcheeky.internal.DreamcheekyBindingConfiguration.TYPE;
import org.openhab.core.items.Item;
import org.openhab.model.item.binding.AbstractGenericBindingProvider;
import org.openhab.model.item.binding.BindingConfigParseException;

/**
 * 
 * Generally its like this:<br>
 * <code>{dreamcheeky="<>(:options)*"}</code>
 * <ul>
 * <li><code>{ dreamcheeky="mailnotifier" }</code></li>
 * <li><code>{ dreamcheeky="mailnotifier:smooth" }</code></li>
 * <li><code>{ dreamcheeky="mailnotifier:straight" }</code></li>
 * 
 * <li><code>{ dreamcheeky="ledmessageboard" }</code></li>
 * <li><code>{ dreamcheeky="ledmessageboard:100" }</code></li>
 * </ul>
 * 
 * @author Heinz
 * 
 */
public class DreamcheekyGenericBindingProvider extends
		AbstractGenericBindingProvider implements DreamcheekyBindingProvider {

	@Override
	public String getBindingType() {
		return "dreamcheeky";
	}
	
	public DreamcheekyBindingConfiguration getConfig(String itemName) {
		return (DreamcheekyBindingConfiguration) bindingConfigs.get(itemName);
	}

	@Override
	public void validateItemType(Item item, String bindingConfig)
			throws BindingConfigParseException {
		new DreamcheekyBindingConfiguration().parse(item, bindingConfig);
	}

	@Override
	public void processBindingConfiguration(String context, Item item,
			String bindingConfig) throws BindingConfigParseException {
		super.processBindingConfiguration(context, item, bindingConfig);
		DreamcheekyBindingConfiguration config = new DreamcheekyBindingConfiguration();
		config.parse(item, bindingConfig);
		addBindingConfig(item, config);
	}
	
	@Override
	public boolean containsBindingForMessageBoard() {
		return containsBindingFor(TYPE.LEDMESSAGEBOARD);
	}

	@Override
	public boolean containsBindingForMailNotifier() {
		return containsBindingFor(TYPE.MAILNOTIFIER);
	}
	
	private boolean containsBindingFor(TYPE type) {
		for (String key: bindingConfigs.keySet()) {
			DreamcheekyBindingConfiguration config = (DreamcheekyBindingConfiguration) bindingConfigs.get(key);
			if (type.equals(config.getType())) {
				return true;
			}
		}
		return false;
	}

}

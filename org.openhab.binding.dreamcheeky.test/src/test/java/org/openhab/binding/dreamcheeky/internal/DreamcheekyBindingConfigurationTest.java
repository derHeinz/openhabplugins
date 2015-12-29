package org.openhab.binding.dreamcheeky.internal;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.openhab.core.items.GroupItem;
import org.openhab.core.library.items.ColorItem;
import org.openhab.core.library.items.LocationItem;
import org.openhab.core.library.items.StringItem;
import org.openhab.core.library.items.SwitchItem;
import org.openhab.model.item.binding.BindingConfigParseException;

public class DreamcheekyBindingConfigurationTest {
	
	@Test
	public void testParseLedmessageboardItems() throws BindingConfigParseException {
		DreamcheekyBindingConfiguration config = new DreamcheekyBindingConfiguration();

		// all good ones
		config.parse(new StringItem(""), "ledmessageboard");

		// some wrong ones
		try {
			config.parse(new GroupItem(""), "ledmessageboard");
			config.parse(new SwitchItem(""), "ledmessageboard");
			config.parse(new LocationItem(""), "ledmessageboard");
			fail();
		} catch (BindingConfigParseException expected) {
			// expected
		}
		
	}
	
	@Test
	public void testParseLedmessageboardOptions() throws BindingConfigParseException {
		DreamcheekyBindingConfiguration config = new DreamcheekyBindingConfiguration();

		// all good ones
		config.parse(new StringItem(""), "ledmessageboard:100");
		
		// wrong ones
		try {
			config.parse(new StringItem(""), "ledmessageboard:100a");
			fail();
		} catch (BindingConfigParseException expected) {
			// expected
		}
		
		try {
			config.parse(new StringItem(""), "ledmessageboard:a");
			fail();
		} catch (BindingConfigParseException expected) {
			// expected
		}
		
		try {
			config.parse(new StringItem(""), "ledmessageboard:");
			fail();
		} catch (BindingConfigParseException expected) {
			// expected
		}

		
		
	}

	@Test
	public void testParseMailnotifierItems() throws BindingConfigParseException {
		DreamcheekyBindingConfiguration config = new DreamcheekyBindingConfiguration();

		// all good ones
		config.parse(new SwitchItem(""), "mailnotifier");
		config.parse(new ColorItem(""), "mailnotifier");

		// some wrong ones
		try {
			config.parse(new GroupItem(""), "mailnotifier");
			fail();
		} catch (BindingConfigParseException expected) {
			// expected
		}
		
		try {
			config.parse(new LocationItem(""), "mailnotifier");
			fail();
		} catch (BindingConfigParseException expected) {
			// expected
		}
	}
	
	@Test
	public void testParseMailNotifierOptions() throws BindingConfigParseException {
		DreamcheekyBindingConfiguration config = new DreamcheekyBindingConfiguration();
		
		// all good ones
		config.parse(new SwitchItem(""), "mailnotifier");
		config.parse(new SwitchItem(""), "mailnotifier:smooth");
		config.parse(new SwitchItem(""), "mailnotifier:straight");
		
		// some wrong ones
		try {
			config.parse(new SwitchItem(""), "mailnotifier:");
			fail();
		} catch (BindingConfigParseException expected) {
			// expected
		}
		
		try {
			config.parse(new SwitchItem(""), "mailnotifier:something");
			fail();
		} catch (BindingConfigParseException expected) {
			// expected
		}
		
		try {
			config.parse(new SwitchItem(""), "mailnotifier:???");
			fail();
		} catch (BindingConfigParseException expected) {
			// expected
		}
		
		try {
			config.parse(new SwitchItem(""), "mailnotifier:smooth:");
			fail();
		} catch (BindingConfigParseException expected) {
			// expected
		}
	}
	
}

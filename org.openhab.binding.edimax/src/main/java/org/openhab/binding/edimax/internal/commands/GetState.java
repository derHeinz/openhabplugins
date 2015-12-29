package org.openhab.binding.edimax.internal.commands;

import java.util.List;

/**
 * Zustand.
 */
public class GetState extends AbstractCMDCommand<Boolean> {
	
	@Override
	protected List<String> getPath() {
		List<String> list = super.getPath();
		list.add("Device.System.Power.State");
		return list;
	}
	
}

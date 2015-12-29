package org.openhab.binding.edimax.internal.commands;

import java.util.List;

public abstract class AbstractCMDNowPowerCommand<T> extends AbstractCMDCommand<T> {

	@Override
	protected List<String> getPath() {
		List<String> list = super.getPath();
		list.add("NOW_POWER");
		return list;
	}
	
}

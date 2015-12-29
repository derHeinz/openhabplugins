package org.openhab.binding.edimax.internal.commands;

import java.util.List;

public abstract class AbstractCMDSYSTEMINOFCommand<T> extends AbstractCMDCommand<T> {
	
	@Override
	protected List<String> getPath() {
		List<String> list = super.getPath();
		list.add("SYSTEM_INFO");
		return list;
	}

}

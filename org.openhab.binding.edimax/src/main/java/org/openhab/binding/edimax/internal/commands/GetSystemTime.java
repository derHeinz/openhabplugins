package org.openhab.binding.edimax.internal.commands;

import java.util.Date;
import java.util.List;

public class GetSystemTime extends AbstractCMDCommand<Date> {
	
	@Override
	protected List<String> getPath() {
		List<String> list = super.getPath();
		list.add("Device.System.Time");
		return list;
	}

}

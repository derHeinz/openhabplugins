package org.openhab.binding.edimax.internal.commands;

import java.util.Date;
import java.util.List;

public class GetLastToggleTime extends AbstractCMDNowPowerCommand<Date> {

	@Override
	protected List<String> getPath() {
		List<String> list = super.getPath();
		list.add("Device.System.Power.LastToggleTime");
		return list;
	}

}

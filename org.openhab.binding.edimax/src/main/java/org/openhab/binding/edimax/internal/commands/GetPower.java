package org.openhab.binding.edimax.internal.commands;

import java.math.BigDecimal;
import java.util.List;

/**
 * Watt.
 */
public class GetPower extends AbstractCMDNowPowerCommand<BigDecimal> {
	
	@Override
	protected List<String> getPath() {
		List<String> list = super.getPath();
		list.add("Device.System.Power.NowPower");
		return list;
	}
	
}

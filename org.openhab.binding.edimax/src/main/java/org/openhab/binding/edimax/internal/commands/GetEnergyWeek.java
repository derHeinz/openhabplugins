package org.openhab.binding.edimax.internal.commands;

import java.math.BigDecimal;
import java.util.List;


public class GetEnergyWeek extends AbstractCMDNowPowerCommand<BigDecimal> {
	
	@Override
	protected List<String> getPath() {
		List<String> list = super.getPath();
		list.add("Device.System.Power.NowEnergy.Week");
		return list;
	}
	
		
}

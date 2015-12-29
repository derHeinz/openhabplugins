package org.openhab.binding.edimax.internal.commands;

import java.util.List;

public class GetMAC extends AbstractCMDSYSTEMINOFCommand<String> {
	
	// Run.LAN.Client.MAC.Address
	
	@Override
	protected List<String> getPath() {
		List<String> list = super.getPath();
		list.add("Run.LAN.Client.MAC.Address");
		return list;
	}

}

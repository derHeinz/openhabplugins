package org.openhab.binding.edimax.internal.commands;

import java.util.List;

public class GetInternet extends AbstractCMDCommand<Integer> {
	
	@Override
	protected List<String> getPath() {
		List<String> list = super.getPath();
		list.add("INTERNET");
		return list;
	}

}

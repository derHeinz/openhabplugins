package org.openhab.binding.edimax.internal.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetNowPowerCommandCompound extends AbstractCMDCommand<Map<AbstractCMDCommand<?>, Object>> {

	private List<AbstractCMDCommand<?>> getCommands = new ArrayList<>();

	public void addCommand(AbstractCMDNowPowerCommand<?> aCommand) {
		getCommands.add(aCommand);
	}

	@Override
	protected String createLeafTag(String aName) {
		// hook in here
		StringBuffer allLeafs = new StringBuffer();
		for (AbstractCMDCommand<?> cmd : getCommands) {
			String lastElement = lastPathSegmentsElement(cmd);
			allLeafs.append(super.createLeafTag(lastElement));
		}
		return allLeafs.toString();
	}

	@Override
	protected List<String> getPath() {
		// hook in here
		return getCommands.get(0).getPath();
	}

	private String lastPathSegmentsElement(AbstractCMDCommand<?> cmd) {
		return (String) cmd.getPath().get(cmd.getPath().size() - 1);
	}

	protected Map<AbstractCMDCommand<?>, Object> getResultValue(String aResponse) {
		Map<AbstractCMDCommand<?>, Object> results = new HashMap<>();
		for (AbstractCMDCommand<?> cmd : getCommands) {
			Object individualResult = cmd.getResultValue(aResponse);
			results.put(cmd, individualResult);
		}
		
		return results;
	}

}

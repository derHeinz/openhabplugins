package org.openhab.binding.edimax.internal.commands;

import java.util.List;


public abstract class AbstractCMDCommand<T> extends AbstractCommand<T> {
	
	/**
	 * GET constructor.
	 */
	public AbstractCMDCommand() {
	}
	
	/**
	 * SET constructor.
	 * @param newValue
	 */
	public AbstractCMDCommand(T newValue) {
		setValue = newValue;
	}
	
	@Override
	protected List<String> getPath() {
		List<String> list = super.getPath();
		list.add("CMD");
		return list;
	}

}

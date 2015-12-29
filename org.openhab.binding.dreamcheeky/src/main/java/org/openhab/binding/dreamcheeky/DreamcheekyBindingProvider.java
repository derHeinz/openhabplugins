package org.openhab.binding.dreamcheeky;

import org.openhab.core.binding.BindingProvider;

public interface DreamcheekyBindingProvider extends BindingProvider  {
	
	/**
	 * Whether a binding for the LED message board is contained.
	 * @return
	 */
	public boolean containsBindingForMessageBoard();

	/**
	 * Whether a binding for the mailnotifier is contained.
	 * @return
	 */
	public boolean containsBindingForMailNotifier();
}

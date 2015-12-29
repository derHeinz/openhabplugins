package org.openhab.binding.superlegend.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuperlegendActivator implements BundleActivator {

	private static Logger logger = LoggerFactory.getLogger(SuperlegendActivator.class);

	@Override
	public void start(BundleContext context) throws Exception {
		// Nothing.
		logger.debug("Superlegend binding has been started.");
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		// Nothing.
		logger.debug("Superlegend binding has been stopped.");
	}
}
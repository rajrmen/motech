package org.motechproject.openmrs.rest;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.springframework.osgi.context.support.OsgiBundleXmlApplicationContext;

public class Activator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
			OsgiBundleXmlApplicationContext ctx = new OsgiBundleXmlApplicationContext(
					new String[] { "applicationOpenMrsWS.xml" });
			ctx.setBundleContext(context);
			ctx.refresh();
		} finally {
			Thread.currentThread().setContextClassLoader(cl);
		}

	}

	@Override
	public void stop(BundleContext context) throws Exception {

	}

}

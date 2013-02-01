package org.motechproject.server.impl;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.hooks.bundle.FindHook;

import java.util.ArrayList;
import java.util.Collection;

public class MotechBundleHook implements FindHook {

    @Override
    public void find(BundleContext context, Collection bundles) {
        // prevent admin from accessing sms-smpp
        if (context.getBundle().getSymbolicName().toLowerCase().contains("motech-admin")) {
            Collection<Object> toRemove = new ArrayList<>();

            for (Object bundleObj : bundles) {
                Bundle bundle = (Bundle) bundleObj;
                if (bundle.getSymbolicName().toLowerCase().contains("smpp")) {
                    toRemove.add(bundle);
                }
            }

            bundles.removeAll(toRemove);
        }
    }
}

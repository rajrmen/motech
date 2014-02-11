package org.motechproject.osgi.web;

import org.motechproject.commons.api.ApplicationContextServiceReferenceUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.context.ApplicationContext;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class ApplicationContextTracker extends ServiceTracker {

    private Set<String> contextsProcessed = Collections.synchronizedSet(new HashSet<String>());

    private final Object lock = new Object();

    public ApplicationContextTracker(BundleContext context) {
        super(context, ApplicationContext.class.getName(), null);
    }

    protected boolean contextInvalidOrProcessed(ServiceReference serviceReference, ApplicationContext applicationContext) {
        return ApplicationContextServiceReferenceUtils.isNotValid(serviceReference)||
                contextsProcessed.contains(applicationContext.getId());
    }

    protected void markAsProcessed(ServiceReference serviceReference) {
        contextsProcessed.add(serviceReference.getBundle().getSymbolicName());
    }

    protected void removeFromProcessed(ServiceReference serviceReference) {
        contextsProcessed.remove(serviceReference.getBundle().getSymbolicName());
    }

    protected Object getLock() {
        return lock;
    }
}

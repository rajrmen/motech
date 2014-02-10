package org.motechproject.osgi.web;

import org.motechproject.commons.api.ApplicationContextServiceReferenceUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ApplicationContextTracker extends ServiceTracker {

    private List<String> contextsProcessed = Collections.synchronizedList(new ArrayList<String>());

    private final Object lock = new Object();

    public ApplicationContextTracker(BundleContext context) {
        super(context, ApplicationContext.class.getName(), null);
    }

    protected boolean contextInvalidOrProcessed(ServiceReference serviceReference, ApplicationContext applicationContext) {
        return ApplicationContextServiceReferenceUtils.isNotValid(serviceReference)||
                contextsProcessed.contains(applicationContext.getId());
    }

    protected void markAsProcessed(ApplicationContext applicationContext) {
        contextsProcessed.add(applicationContext.getId());
    }

    protected void removeFromProcessed(ApplicationContext applicationContext) {
        contextsProcessed.remove(applicationContext.getId());
    }

    protected Object getLock() {
        return lock;
    }
}

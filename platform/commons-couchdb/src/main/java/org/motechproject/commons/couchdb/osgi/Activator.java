package org.motechproject.commons.couchdb.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.Properties;

public class Activator implements BundleActivator {

    private ApplicationContextTracker applicationContextTracker;
    private ServiceRegistration serviceRegistration;

    @Override
    public void start(BundleContext context) {
        DbSetUpServiceImpl designDocumentInitializationService = new DbSetUpServiceImpl();
        serviceRegistration = context.registerService(DbSetUpService.class.getName(), designDocumentInitializationService, new Properties());
        applicationContextTracker = new ApplicationContextTracker(context, designDocumentInitializationService);
        applicationContextTracker.open();
    }

    @Override
    public void stop(BundleContext context) {
        if (serviceRegistration != null) {
            context.ungetService(serviceRegistration.getReference());
        }
        if (applicationContextTracker != null) {
            applicationContextTracker.close();
        }
    }
}

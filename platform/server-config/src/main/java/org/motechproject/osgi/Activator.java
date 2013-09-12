package org.motechproject.osgi;

import org.apache.felix.cm.PersistenceManager;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.motechproject.server.config.repository.ConfigurationStore;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

import java.util.Properties;

public class Activator implements BundleActivator {

    private ServiceRegistration serviceRegistration;
    private BlueprintApplicationContextTracker applicationContextTracker;

    @Override
    public void start(BundleContext context) throws Exception {
        String pid = OsgiStringUtils.nullSafeSymbolicName(context.getBundle());
        serviceRegistration = registerPersistenceManager(context, pid);
        applicationContextTracker = new BlueprintApplicationContextTracker(context);
        applicationContextTracker.open();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (serviceRegistration != null) {
            context.ungetService(serviceRegistration.getReference());
        }
        if (applicationContextTracker != null) {
            applicationContextTracker.close();
        }
    }

    private ServiceRegistration registerPersistenceManager(BundleContext context, String pid) {
        Properties properties = new Properties();
        properties.put(Constants.SERVICE_PID, pid);
        properties.put(Constants.SERVICE_RANKING, Integer.MAX_VALUE);
        properties.put(Constants.SERVICE_DESCRIPTION, "Platform In Memory Persistence Manager");
        return context.registerService(PersistenceManager.class.getName(), new ConfigurationStore(), properties);
    }
}

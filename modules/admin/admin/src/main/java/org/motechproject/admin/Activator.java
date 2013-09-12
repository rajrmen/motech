package org.motechproject.admin;

import org.apache.felix.cm.PersistenceManager;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.motechproject.server.config.repository.ConfigurationStore;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;

public class Activator implements BundleActivator {

    private static Logger logger = LoggerFactory.getLogger(Activator.class);

    @Override
    public void start(BundleContext context) throws Exception {
        logger.error(String.format("Started bundle: [%d] %s", context.getBundle().getBundleId(),
                context.getBundle().getSymbolicName()));

        String pid = OsgiStringUtils.nullSafeSymbolicName(context.getBundle());

        registerPersistenceManager(context, pid);


        Dictionary<String, String> props = new Hashtable<>();
        props.put(Constants.SERVICE_PID, pid);
        context.registerService(ManagedService.class.getName(), new BundleConfig(), props);


        logger.error("Added BundleConfig with pid " + pid);

        new ConfigAdminServiceTracker(context).open();
    }

    private void registerPersistenceManager(BundleContext context, String pid) {
        Properties properties = new Properties();
        properties.put(Constants.SERVICE_PID, pid);
        properties.put(Constants.SERVICE_DESCRIPTION, "Platform In Memory Persistence Manager");
        properties.put(Constants.SERVICE_RANKING, Integer.MAX_VALUE);
        context.registerService(PersistenceManager.class.getName(), new ConfigurationStore(), properties);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }
}

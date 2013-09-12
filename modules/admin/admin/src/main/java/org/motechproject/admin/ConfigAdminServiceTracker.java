package org.motechproject.admin;

import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class ConfigAdminServiceTracker extends ServiceTracker {

    private static Logger logger = LoggerFactory.getLogger(ConfigAdminServiceTracker.class);

    public ConfigAdminServiceTracker(BundleContext context) {
        super(context, ConfigurationAdmin.class.getName(), null);
    }

    @Override
    public Object addingService(ServiceReference reference) {
        ConfigurationAdmin configurationAdmin = (ConfigurationAdmin) super.addingService(reference);
        try {
            String pid = OsgiStringUtils.nullSafeSymbolicName(context.getBundle());
            logger.info(String.format("Looking for configuration with pid : %s ", pid));
            Configuration configuration = configurationAdmin.getConfiguration(pid);

            Properties properties = new Properties();
            properties.put(Constants.SERVICE_PID, pid);
            properties.put("some.property", "some.value");
            properties.put("some.property2", "some.value2");
            configuration.update(properties);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return configurationAdmin;
    }
}

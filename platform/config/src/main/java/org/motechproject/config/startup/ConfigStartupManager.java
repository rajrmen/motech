package org.motechproject.config.startup;

import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.motechproject.commons.api.ApplicationContextServiceReferenceUtils;
import org.motechproject.config.domain.MotechConfig;
import org.motechproject.config.service.ConfigService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Properties;

public class ConfigStartupManager extends ServiceTracker {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigStartupManager.class);
    private ConfigService configService;
    private BundlePropertiesReader bundlePropertiesReader;


    public ConfigStartupManager(BundleContext bundleContext, ConfigService configService, BundlePropertiesReader bundlePropertiesReader) {
        super(bundleContext, ApplicationContext.class.getName(), null);
        this.configService = configService;
        this.bundlePropertiesReader = bundlePropertiesReader;
    }

    @Override
    public Object addingService(ServiceReference serviceReference) {
        Object service = super.addingService(serviceReference);
        if (ApplicationContextServiceReferenceUtils.isValid(serviceReference)) {
            String bundleSymbolicName = OsgiStringUtils.nullSafeSymbolicName(serviceReference.getBundle());
            Properties properties = bundlePropertiesReader.read(bundleSymbolicName);
            if (!properties.isEmpty()) {
                MotechConfig config = new MotechConfig(bundleSymbolicName, properties);
                LOGGER.debug("Persisting configuration in database. MotechConfig: " + config);
                configService.createConfig(config);
            }
        }
        return service;
    }

}

package org.motechproject.config.startup;

import org.eclipse.gemini.blueprint.service.exporter.OsgiServiceRegistrationListener;
import org.motechproject.config.service.ConfigService;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class ConfigServiceRegistrationListener implements OsgiServiceRegistrationListener {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigServiceRegistrationListener.class);

    private BundleContext bundleContext;
    private ConfigStartupManager tracker;

    @Autowired
    public ConfigServiceRegistrationListener(BundleContext bundleContext) {
        LOG.info("Starting ConfigService registration listener");
        this.bundleContext = bundleContext;
    }

    @Override
    public void registered(Object service, Map serviceProperties) {
        if (service instanceof ConfigService && tracker == null) {
            LOG.info("ConfigService registered. Starting ConfigStartupManager");
            tracker = new ConfigStartupManager(bundleContext, (ConfigService) service);
            tracker.open(true);
        }
    }

    @Override
    public void unregistered(Object service, Map serviceProperties) {
        if (service instanceof ConfigService && tracker != null) {
            tracker.close();
        }
    }
}

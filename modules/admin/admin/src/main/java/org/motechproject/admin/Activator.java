package org.motechproject.admin;

import org.motechproject.server.config.domain.BundleConfig;
import org.motechproject.server.config.service.PlatformConfigurationService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator {

    private static final Logger logger = LoggerFactory.getLogger(Activator.class);
    private ServiceTracker serviceTracker;

    @Override
    public void start(BundleContext context) throws Exception {
        serviceTracker = new ServiceTracker(context, PlatformConfigurationService.class.getName(), null) {
            @Override
            public Object addingService(ServiceReference reference) {
                logger.error("$$$$  Platform config service added $$$$$$$$$");
                PlatformConfigurationService platformConfigService = (PlatformConfigurationService) super.addingService(reference);
                BundleConfig bundleConfig = new BundleConfig();
                bundleConfig.add("message", "Hello World");
                bundleConfig.add("krap.key", "krap.value");
                platformConfigService.store("test", bundleConfig);
                return platformConfigService;
            }
        };
        serviceTracker.open();
    }


    @Override
    public void stop(BundleContext context) throws Exception {

    }
}

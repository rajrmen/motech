package org.motechproject.server.config.osgi;

import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.server.config.settings.MotechSettings;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.ServiceReference;

import java.util.Arrays;
import java.util.List;

public class ConfigBundleIT extends BaseOsgiIT {

    public void testConfigBundle() throws Exception {
        ServiceReference settingsReference = bundleContext.getServiceReference(PlatformSettingsService.class.getName());
        assertNotNull(settingsReference);
        PlatformSettingsService settings = (PlatformSettingsService) bundleContext.getService(settingsReference);
        assertNotNull(settings);

        final MotechSettings platformSettings = settings.getPlatformSettings();
        final String brokerUrl = platformSettings.getActivemqProperties().getProperty("broker.url");
        assertEquals("tcp://localhost:61616", brokerUrl);
    }

    @Override
    protected List<String> getImports() {
        return Arrays.asList("org.motechproject.server.config", "org.motechproject.commons.couchdb.service",
                "org.motechproject.server.config.settings, org.motechproject.server.config.service");
    }
}

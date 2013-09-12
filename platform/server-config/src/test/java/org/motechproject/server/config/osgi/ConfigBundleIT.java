package org.motechproject.server.config.osgi;

import org.eclipse.gemini.blueprint.test.platform.OsgiPlatform;
import org.motechproject.server.config.domain.BundleConfig;
import org.motechproject.server.config.service.PlatformConfigurationService;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.server.config.settings.MotechSettings;
import org.motechproject.server.config.test.domain.TestConfigUpdater;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.motechproject.testing.utils.Wait;
import org.motechproject.testing.utils.WaitCondition;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.Manifest;

public class ConfigBundleIT extends BaseOsgiIT {

    public void testConfigBundle() throws Exception {
        ServiceReference settingsReference = bundleContext.getServiceReference(PlatformSettingsService.class.getName());
        assertNotNull(settingsReference);
        PlatformSettingsService settings = (PlatformSettingsService) bundleContext.getService(settingsReference);
        assertNotNull(settings);
        settings.setActiveMqSetting("call.delay", "5000");

        settings.evictMotechSettingsCache();

        final MotechSettings platformSettings = settings.getPlatformSettings();
        final String delay = platformSettings.getActivemqProperties().getProperty("call.delay");
        assertEquals("5000", delay);
    }


    public void testThatPlatformConfigurationServiceIsAvailable() throws InterruptedException {
        PlatformConfigurationService platformConfigurationService = (PlatformConfigurationService) getApplicationContext().getBean("platformConfigurationService");
        assertNotNull(platformConfigurationService);
        BundleConfig fooConfig = platformConfigurationService.get("foo");

        assertNull(fooConfig);

        BundleConfig bundleConfig = new BundleConfig();
        bundleConfig.add("bar", "hello");
        platformConfigurationService.store("foo", bundleConfig);

        BundleConfig updatedFooConfig = platformConfigurationService.get("foo");
        assertEquals("hello", updatedFooConfig.get("bar"));

        TestConfigUpdater testUpdater = (TestConfigUpdater) getApplicationContext().getBean("testUpdater");
        final Map<String, String> existingProperties = testUpdater.getExistingProperties();


        new Wait(new WaitCondition() {
            @Override
            public boolean needsToWait() {
                return existingProperties.isEmpty();
            }
        }, 2000).start();

        assertFalse(existingProperties.isEmpty());


    }

    @Override
    protected List<String> getImports() {
        return Arrays.asList("org.motechproject.server.config.domain",
                "org.motechproject.commons.couchdb.service",
                "org.motechproject.server.config.annotations",
                "org.motechproject.server.config.settings");
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"testApplicationPlatformConfig.xml"};
    }

    @Override
    protected Manifest getManifest() {
        Manifest manifest = super.getManifest();

        String originalExports = manifest.getMainAttributes().getValue(Constants.EXPORT_PACKAGE);
        String exports = "org.motechproject.server.config.test.domain," + ((originalExports == null) ? "" : originalExports);
        manifest.getMainAttributes().putValue(Constants.EXPORT_PACKAGE, exports);

        return manifest;
    }

    @Override
    protected OsgiPlatform createPlatform() {
        OsgiPlatform platform = super.createPlatform();
        Properties properties = platform.getConfigurationProperties();
        properties.setProperty("felix.cm.dir", "/tmp/osgi");

        return platform;
    }
}

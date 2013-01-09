package org.motechproject.tasks.osgi;

import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.domain.DataProvider;
import org.motechproject.tasks.repository.AllChannels;
import org.motechproject.tasks.repository.AllDataProviders;
import org.motechproject.tasks.service.ChannelService;
import org.motechproject.tasks.service.DataProviderService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.ServiceReference;

import java.util.List;

import static java.util.Arrays.asList;

public class TasksBundleIT extends BaseOsgiIT {
    private static final Integer TRIES_COUNT = 50;

    public void testCoreServiceReferences() {
        assertNotNull(bundleContext.getServiceReference(PlatformSettingsService.class.getName()));
        assertNotNull(bundleContext.getServiceReference(EventListenerRegistryService.class.getName()));
        assertNotNull(bundleContext.getServiceReference(EventRelay.class.getName()));
    }

    public void testChannelService() throws InterruptedException {
        ChannelService channelService = getService(ChannelService.class);
        Channel fromFile;
        int tries = 0;

        do {
            fromFile = channelService.getChannel("test", "test", "0.15");
            ++tries;
            Thread.sleep(500);
        } while (fromFile == null && tries < TRIES_COUNT);

        assertNotNull(fromFile);

        AllChannels allChannels = getApplicationContext().getBean(AllChannels.class);
        Channel fromDB = allChannels.byChannelInfo("test", "test", "0.15");

        assertNotNull(fromDB);
        assertEquals(fromDB, fromFile);

        allChannels.remove(fromDB);

        fromDB = allChannels.byChannelInfo("test", "test", "0.15");
        assertNull(fromDB);
    }

    public void testDataProviderService() throws InterruptedException {
        DataProviderService dataProviderService = getService(DataProviderService.class);
        DataProvider fromFile;
        int tries = 0;

        do {
            fromFile = dataProviderService.getProvider("MRS");
            ++tries;
            Thread.sleep(500);
        } while (fromFile == null && tries < TRIES_COUNT);

        assertNotNull(fromFile);

        AllDataProviders allDataProviders = getApplicationContext().getBean(AllDataProviders.class);
        DataProvider fromDB = allDataProviders.byName("MRS");

        assertNotNull(fromDB);
        assertEquals(fromDB, fromFile);

        allDataProviders.remove(fromDB);

        fromDB = allDataProviders.byName("MRS");
        assertNull(fromDB);
    }

    @Override
    protected List<String> getImports() {
        return asList("org.motechproject.tasks.util", "org.motechproject.server.config", "org.motechproject.commons.api");
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"/META-INF/osgi/testApplicationTasksBundle.xml"};
    }

    private <T> T getService(Class<T> clazz) throws InterruptedException {
        T service = clazz.cast(bundleContext.getService(getServiceReference(clazz)));

        assertNotNull(service);

        return service;
    }

    private <T> ServiceReference getServiceReference(Class<T> clazz) throws InterruptedException {
        ServiceReference serviceReference;
        int tries = 0;

        do {
            serviceReference = bundleContext.getServiceReference(clazz.getName());
            ++tries;
            Thread.sleep(500);
        } while (serviceReference == null && tries < TRIES_COUNT);

        assertNotNull(serviceReference);

        return serviceReference;
    }

}

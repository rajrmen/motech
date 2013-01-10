package org.motechproject.mrs.osgi;

import org.motechproject.commons.api.DataProviderLookup;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.mrs.MRSDataProviderLookup;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.ServiceReference;

import java.util.Arrays;
import java.util.List;

public class MRSBundleIT extends BaseOsgiIT {

    public void testMRSApiBundle() {
        assertNotNull(bundleContext.getServiceReference(EventListenerRegistryService.class.getName()));

        ServiceReference serviceReference = bundleContext.getServiceReference(DataProviderLookup.class.getName());
        assertNotNull(serviceReference);

        MRSDataProviderLookup providerLookup = (MRSDataProviderLookup) bundleContext.getService(serviceReference);
        assertNotNull(providerLookup);

        List<Class<?>> classes = Arrays.asList(MRSPerson.class, MRSPatient.class, MRSFacility.class);

        for (Class<?> cls : classes) {
            assertTrue(providerLookup.supports(cls.getName()));
        }
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"/META-INF/osgi/testApplicationMrsBundle.xml"};
    }
}

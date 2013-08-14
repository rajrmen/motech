package org.motechproject.event.osgi;

import org.junit.Ignore;

import org.motechproject.event.listener.EventListenerRegistry;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.motechproject.testing.utils.Wait;
import org.motechproject.testing.utils.WaitCondition;

import static org.motechproject.testing.utils.event.listener.FakeEventListener.SUBJECT_FOR_ONE_LISTENER_A;
import static org.motechproject.testing.utils.event.listener.FakeEventListener.SUBJECT_FOR_ONE_LISTENER_B;
import static org.motechproject.testing.utils.event.listener.FakeEventListener.SUBJECT_FOR_TWO_LISTENERS;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

import static org.osgi.framework.Bundle.ACTIVE;
import static org.osgi.framework.Bundle.RESOLVED;
import static org.osgi.framework.Bundle.INSTALLED;
import static org.osgi.framework.Bundle.UNINSTALLED;

import java.util.Arrays;
import java.util.List;

public class EventRegistryBundleLifecycleIT extends BaseOsgiIT {

    @Ignore
    public void testListenerRegistrationStopStart() throws Exception {
        verifyListenersRegistered();
        
        Bundle testingUtilsBundle = getTestingUtilsBundle();

        testingUtilsBundle.stop();
        waitForBundleState(testingUtilsBundle, RESOLVED);
        verifyListenersCleared();

        testingUtilsBundle.start();
        waitForBundleState(testingUtilsBundle, ACTIVE);
        verifyListenersRegistered();
    }

    public void testListenerRegistrationUninstallInstall() throws Exception {
        verifyListenersRegistered();
        
        Bundle testingUtilsBundle = getTestingUtilsBundle();
        String testingUtilsLocation = testingUtilsBundle.getLocation();

        testingUtilsBundle.uninstall();
        waitForBundleState(testingUtilsBundle, UNINSTALLED);
        verifyListenersCleared();

        testingUtilsBundle = bundleContext.installBundle(testingUtilsLocation);
        waitForBundleState(testingUtilsBundle, INSTALLED);        
        testingUtilsBundle.start();
        waitForBundleState(testingUtilsBundle, ACTIVE);
        verifyListenersRegistered();
    }

    private void verifyListenersRegistered() {
        EventListenerRegistry registry = getEventRegistry();
        assertEquals(1, registry.getListenerCount(SUBJECT_FOR_ONE_LISTENER_A));
        assertEquals(1, registry.getListenerCount(SUBJECT_FOR_ONE_LISTENER_B));
        assertEquals(2, registry.getListenerCount(SUBJECT_FOR_TWO_LISTENERS));
    }

    private void verifyListenersCleared() {
        EventListenerRegistry registry = getEventRegistry();
        assertEquals(0, registry.getListenerCount(SUBJECT_FOR_ONE_LISTENER_A));
        assertEquals(0, registry.getListenerCount(SUBJECT_FOR_ONE_LISTENER_B));
        assertEquals(0, registry.getListenerCount(SUBJECT_FOR_TWO_LISTENERS));
    }
    
    private EventListenerRegistry getEventRegistry() {
        ServiceReference registryReference = bundleContext.getServiceReference(EventListenerRegistryService.class.getName());
        assertNotNull(registryReference);
        EventListenerRegistry registry = (EventListenerRegistry) bundleContext.getService(registryReference);
        assertNotNull(registry);
        return registry;
    }

    private Bundle getTestingUtilsBundle() {
        Bundle testingUtilsBundle = null;
        for (Bundle bundle : bundleContext.getBundles()) {
            if ("org.motechproject.motech-testing-utils".equals(bundle.getSymbolicName()) && UNINSTALLED != bundle.getState()) {
                testingUtilsBundle = bundle;
                break;
            }
        }
        assertNotNull(testingUtilsBundle);
        return testingUtilsBundle;
    }

    private void waitForBundleState(final Bundle bundle, final int state) throws Exception {
        new Wait(new WaitCondition() {
            @Override
            public boolean needsToWait() {
                return state == bundle.getState();
            }
        }, 2000).start();
        assertEquals(state, bundle.getState());
    }

    @Override
    protected List<String> getImports() {
        return Arrays.asList("org.motechproject.event", "org.motechproject.event.listener",
                "org.motechproject.event.listener.annotations");
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[] { "/META-INF/osgi/testEventBundleContext.xml" };
    }
}

package org.motechproject.mds.osgi;

import org.motechproject.bundle.extender.MotechOsgiConfigurableApplicationContext;
import org.motechproject.mds.annotations.internal.MDSAnnotationProcessor;
import org.motechproject.osgi.web.ApplicationContextTracker;
import org.motechproject.osgi.web.MotechOsgiWebApplicationContext;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.wiring.FrameworkWiring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * The <code>MDSApplicationContextTracker</code> in Motech Data Services listens to the service
 * registrations and passes application contexts to the MDSAnnotationProcess for annotation
 * scanning
 */
@Component
public class MDSApplicationContextTracker {
    private static final Logger LOGGER = LoggerFactory.getLogger(MDSApplicationContextTracker.class);
    private static final int CONTEXT_WAIT_TIME = 5000;

    private MDSServiceTracker serviceTracker;

    private MDSAnnotationProcessor processor;

    @PostConstruct
    public void startTracker() {
        Bundle bundle = FrameworkUtil.getBundle(this.getClass());

        if (null == serviceTracker && bundle != null) {
            serviceTracker = new MDSServiceTracker(bundle.getBundleContext());
            serviceTracker.open();
        }
    }

    @PreDestroy
    public void stopTracker() {
        if (null != serviceTracker) {
            serviceTracker.close();
        }
    }

    @Autowired
    public void setProcessor(MDSAnnotationProcessor processor) {
        this.processor = processor;
    }

    private class MDSServiceTracker extends ApplicationContextTracker {

        private Set<String> bundlesRefreshed = new HashSet<>();

        public MDSServiceTracker(BundleContext bundleContext) {
            super(bundleContext);
        }

        @Override
        public Object addingService(ServiceReference serviceReference) {
            ApplicationContext applicationContext = (ApplicationContext) super.addingService(serviceReference);
            LOGGER.debug("Starting to process {}", applicationContext.getDisplayName());

            synchronized (getLock()) {
                if (contextInvalidOrProcessed(serviceReference, applicationContext)) {
                    return applicationContext;
                }
                markAsProcessed(serviceReference);

                process(serviceReference, applicationContext);
            }

            LOGGER.debug("Processed {}", applicationContext.getDisplayName());
            return applicationContext;
        }

        private void process(ServiceReference serviceReference, ApplicationContext applicationContext) {
            Bundle bundle = serviceReference.getBundle();
            String symbolicName = bundle.getSymbolicName();

            if (bundlesRefreshed.contains(symbolicName)) {
                // the refresh came from us
                LOGGER.debug("Bundle {} was already processed, ignoring");
                // next time we want to process it, since the refresh won't come from us
                bundlesRefreshed.remove(symbolicName);
            } else {
                // process
                boolean annotationsFound = processor.processAnnotations(bundle);
                // if we found annotations, we will refresh the bundle in order to start weaving the classes it exposes
                if (annotationsFound) {
                    refresh(bundle, applicationContext);
                }
            }
        }

        private void refresh(Bundle bundleToRefresh, ApplicationContext applicationContext) {
            waitForContext(applicationContext, bundleToRefresh);

            Bundle frameworkBundle = context.getBundle(0);
            FrameworkWiring frameworkWiring = frameworkBundle.adapt(FrameworkWiring.class);

            bundlesRefreshed.add(bundleToRefresh.getSymbolicName());

            frameworkWiring.refreshBundles(Arrays.asList(bundleToRefresh));
        }

        @Override
        public void removedService(ServiceReference reference, Object service) {
            super.removedService(reference, service);

            synchronized (getLock()) {
                removeFromProcessed(reference);
            }
        }
    }

    private void waitForContext(ApplicationContext applicationContext, Bundle bundle) {
        if (applicationContext instanceof MotechOsgiWebApplicationContext) {
            MotechOsgiWebApplicationContext motechOsgiWebApplicationContext =
                    (MotechOsgiWebApplicationContext) applicationContext;
            if (!motechOsgiWebApplicationContext.isInitialized()) {
                try {
                    synchronized (motechOsgiWebApplicationContext.getLock()) {
                        motechOsgiWebApplicationContext.getLock().wait(CONTEXT_WAIT_TIME);
                    }
                } catch (InterruptedException e) {
                    if (!motechOsgiWebApplicationContext.isInitialized()) {
                        LOGGER.warn("Refreshing context for bundle {} before it was initialized",
                                bundle.getSymbolicName());
                    }
                }
            }
        } else if (applicationContext instanceof MotechOsgiConfigurableApplicationContext) {
            MotechOsgiConfigurableApplicationContext motechOsgiWebApplicationContext =
                    (MotechOsgiConfigurableApplicationContext) applicationContext;
            if (!motechOsgiWebApplicationContext.isInitialized()) {
                try {
                    synchronized (motechOsgiWebApplicationContext.getLock()) {
                        motechOsgiWebApplicationContext.getLock().wait(CONTEXT_WAIT_TIME);
                    }
                } catch (InterruptedException e) {
                    if (!motechOsgiWebApplicationContext.isInitialized()) {
                        LOGGER.warn("Refreshing context for bundle {} before it was initialized",
                                bundle.getSymbolicName());
                    }
                }
            }
        }
    }
}

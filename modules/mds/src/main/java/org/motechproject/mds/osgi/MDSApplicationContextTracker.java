package org.motechproject.mds.osgi;

import org.motechproject.mds.annotations.internal.MDSAnnotationProcessor;
import org.motechproject.osgi.web.ApplicationContextTracker;
import org.motechproject.osgi.web.MotechOsgiWebApplicationContext;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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

    private MDSServiceTracker serviceTracker;

    private MDSAnnotationProcessor processor;
    private PackageAdmin packageAdmin;

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

    @Autowired
    public void setPackageAdmin(PackageAdmin packageAdmin) {
        this.packageAdmin = packageAdmin;
    }

    private class MDSServiceTracker extends ApplicationContextTracker {

        private Set<String> bundlesRefreshed = new HashSet<>();

        public MDSServiceTracker(BundleContext bundleContext) {
            super(bundleContext, MotechOsgiWebApplicationContext.class);
        }

        @Override
        public Object addingService(ServiceReference serviceReference) {
            ApplicationContext applicationContext = (ApplicationContext) super.addingService(serviceReference);
            LOGGER.debug("Starting to process {}", applicationContext.getDisplayName());

            synchronized (getLock()) {
                if (contextInvalidOrProcessed(serviceReference)) {
                    return applicationContext;
                }
                markAsProcessed(serviceReference);

                process(serviceReference);
            }

            LOGGER.debug("Processed {}", applicationContext.getDisplayName());
            return applicationContext;
        }

        private void process(ServiceReference serviceReference) {
            Bundle bundle = serviceReference.getBundle();

            boolean annotationsFound = processor.processAnnotations(bundle);
            // if we found annotations, we will refresh the bundle in order to start weaving the classes it exposes
            if (annotationsFound) {
                // TODO: use FrameworkWiring
                // We use a deprecated method from the package admin in order to avoid compile time issues
                // since we have osgi.core 4.2.0 on the classpath. We cannot simply switch to 4.3.0 because
                // of issues with OSGi ITs. Until they are resolved, we have to rely on the PackageAdmin.
                packageAdmin.refreshPackages(new Bundle[]{bundle});
            }
        }

        @Override
        public void removedService(ServiceReference reference, Object service) {
            super.removedService(reference, service);

            String bundleSymbolicName = reference.getBundle().getSymbolicName();

            synchronized (getLock()) {
                if (bundlesRefreshed.contains(bundleSymbolicName)) {
                    // the refresh came from us
                    LOGGER.debug("Bundle {} was already processed, ignoring", bundleSymbolicName);
                    // next time we want to process it, since the refresh won't come from us
                    bundlesRefreshed.remove(bundleSymbolicName);
                } else {
                    // bundle was stopped/removed by a 3rd party
                    removeFromProcessed(reference);
                }
            }
        }
    }
}

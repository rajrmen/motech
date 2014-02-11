package org.motechproject.mds.annotations.internal;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.FrameworkWiring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * The <code>SeussAnnotationProcessor</code> class is responsible for scanning bundle contexts and
 * looking for classes, fields and methods containing Seuss annotations, as well as processing them.
 *
 * @see org.motechproject.mds.annotations.Lookup
 * @see org.motechproject.mds.annotations.Entity
 */
@Component
public class SeussAnnotationProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(SeussAnnotationProcessor.class);

    private EntityProcessor entityProcessor;
    private LookupProcessor lookupProcessor;
    private BundleContext bundleContext;

    public void processAnnotations(Bundle bundle) {
        LOGGER.debug("Starting scanning bundle {} for MDS annotations.", bundle.getSymbolicName());

        boolean annotationsFound = entityProcessor.execute(bundle);
        annotationsFound |= lookupProcessor.execute(bundle);

        if (annotationsFound) {
            LOGGER.info("Refreshing wiring for {} after annotation processing", bundle.getSymbolicName());
            refresh(bundle);
        }

        LOGGER.debug("Finished scanning bundle {} for MDS annotations.", bundle.getSymbolicName());
    }

    private void refresh(Bundle bundleToRefresh) {
        Bundle frameworkBundle = bundleContext.getBundle(0);
        FrameworkWiring frameworkWiring = frameworkBundle.adapt(FrameworkWiring.class);
        frameworkWiring.refreshBundles(Arrays.asList(bundleToRefresh));
    }

    @Autowired
    public void setLookupProcessor(LookupProcessor lookupProcessor) {
        this.lookupProcessor = lookupProcessor;
    }

    @Autowired
    public void setEntityProcessor(EntityProcessor entityProcessor) {
        this.entityProcessor = entityProcessor;
    }

    @Autowired
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }
}

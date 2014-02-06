package org.motechproject.mds.osgi;


import org.osgi.framework.hooks.resolver.ResolverHook;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class MdsResolverHook implements ResolverHook {

    private static final Logger LOG = LoggerFactory.getLogger(MdsResolverHook.class);

    public void filterResolvable(Collection<BundleRevision> bundleRevisions) {
        LOG.trace("Filter resolvable called");
    }

    @Override
    public void filterSingletonCollisions(BundleCapability bundleCapability, Collection<BundleCapability> bundleCapabilities) {
        //LOG.trace("Filter ");
    }

    @Override
    public void filterMatches(BundleRequirement bundleRequirement, Collection<BundleCapability> bundleCapabilities) {

    }

    @Override
    public void end() {

    }
}

package org.motechproject.server.config.service;

import org.motechproject.server.config.domain.BundleConfig;

public interface PlatformConfigurationService {

    BundleConfig get(String bundleSymbolicName);

    void store(String bundleSymbolicName, BundleConfig bundleConfig);

}

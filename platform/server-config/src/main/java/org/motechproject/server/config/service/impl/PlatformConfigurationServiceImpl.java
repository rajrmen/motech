package org.motechproject.server.config.service.impl;

import org.motechproject.server.config.domain.BootstrapConfig;
import org.motechproject.server.config.bootstrap.BootstrapConfigLoader;
import org.motechproject.server.config.service.PlatformConfigurationService;

public class PlatformConfigurationServiceImpl implements PlatformConfigurationService {
    private BootstrapConfigLoader bootstrapConfigLoader;

    public PlatformConfigurationServiceImpl(BootstrapConfigLoader bootstrapConfigLoader) {
        this.bootstrapConfigLoader = bootstrapConfigLoader;
    }

    @Override
    public BootstrapConfig getBootstrapConfig() {
        return bootstrapConfigLoader.getBootstrapConfig();
    }
}

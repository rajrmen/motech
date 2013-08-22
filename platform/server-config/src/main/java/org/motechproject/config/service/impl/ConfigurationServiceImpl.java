package org.motechproject.config.service.impl;

import org.motechproject.config.domain.BootstrapConfig;
import org.motechproject.config.bootstrap.BootstrapConfigLoader;
import org.motechproject.config.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Default implementation of {@link org.motechproject.config.service.ConfigurationService}.
 */
@Component
public class ConfigurationServiceImpl implements ConfigurationService {
    private BootstrapConfigLoader bootstrapConfigLoader;

    @Autowired
    public ConfigurationServiceImpl(BootstrapConfigLoader bootstrapConfigLoader) {
        this.bootstrapConfigLoader = bootstrapConfigLoader;
    }

    @Override
    public BootstrapConfig loadBootstrapConfig() {
        return bootstrapConfigLoader.loadBootstrapConfig();
    }
}

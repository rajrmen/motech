package org.motechproject.server.config.service;

import org.motechproject.server.config.domain.BootstrapConfig;

public interface PlatformConfigurationService {
    BootstrapConfig getBootstrapConfig();
}

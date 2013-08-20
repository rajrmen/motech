package org.motechproject.server.config.bootstrap;

import org.motechproject.server.config.domain.BootstrapConfig;

public interface BootstrapConfigLoader {
    //TODO: declare throws MotechConfigurationException in javadoc
    BootstrapConfig getBootstrapConfig();
}

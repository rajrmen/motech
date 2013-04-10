package org.motechproject.config.service;

import org.motechproject.config.domain.MotechConfig;

public interface ConfigService {

    void createConfig(MotechConfig config);

    String findConfig(String key, String module);
}
package org.motechproject.config.service.impl;

import org.motechproject.config.domain.MotechConfig;
import org.motechproject.config.repository.AllMotechConfigs;
import org.motechproject.config.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("configService")
public class ConfigServiceImpl implements ConfigService {

    @Autowired
    private AllMotechConfigs allConfigs;

    @Override
    public void createConfig(MotechConfig config) {
        allConfigs.addOrReplace(config);
    }

    @Override
    public String findConfig(String key, String module) {
        return allConfigs.findConfig(key, module);
    }
}

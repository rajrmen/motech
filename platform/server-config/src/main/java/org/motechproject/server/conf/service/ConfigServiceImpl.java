package org.motechproject.server.conf.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfigServiceImpl implements ConfigService {

    @Autowired
    private AllMotechConfigs allConfigs;

    @Override
    public void createConfig(MotechConfig config) {
        allConfigs.add(config);
    }

    @Override
    public String findConfig(String key, String module) {
        return allConfigs.findConfig(key, module);
    }
}

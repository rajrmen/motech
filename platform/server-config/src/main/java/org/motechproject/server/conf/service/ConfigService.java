package org.motechproject.server.conf.service;

public interface ConfigService {

    void createConfig(MotechConfig config);

    String findConfig(String key, String module);
}
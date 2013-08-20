package org.motechproject.server.config.bootstrap.impl;

import org.motechproject.server.config.bootstrap.Environment;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentImpl implements Environment {
    @Override
    public String getValue(String variableName) {
        return System.getenv(variableName);
    }
}

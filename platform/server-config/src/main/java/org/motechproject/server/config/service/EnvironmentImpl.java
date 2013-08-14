package org.motechproject.server.config.service;

public class EnvironmentImpl implements Environment {
    @Override
    public String getValue(String variableName) {
        return System.getenv(variableName);
    }
}

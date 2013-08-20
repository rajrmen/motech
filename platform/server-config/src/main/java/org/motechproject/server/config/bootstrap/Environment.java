package org.motechproject.server.config.bootstrap;

public interface Environment {
    String MOTECH_CONFIG_DIR = "MOTECH_CONFIG_DIR";
    String MOTECH_DB_URL = "MOTECH_DB_URL";
    String MOTECH_DB_USERNAME = "MOTECH_DB_USERNAME";
    String MOTECH_DB_PASSWORD = "MOTECH_DB_PASSWORD";
    String MOTECH_TENANT_ID = "MOTECH_TENANT_ID";
    String MOTECH_CONFIG_SOURCE = "MOTECH_CONFIG_SOURCE";

    String getValue(String variableName);
}

package org.motechproject.server.config.service.impl;

import org.motechproject.server.config.domain.BootstrapConfig;
import org.motechproject.server.config.service.BootstrapConfigLoader;
import org.motechproject.server.config.service.ConfigFileReader;
import org.motechproject.server.config.service.Environment;
import org.motechproject.server.config.service.MotechConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class BootstrapConfigLoaderImpl implements BootstrapConfigLoader {
    static final String MOTECH_CONFIG_DIR = "MOTECH_CONFIG_DIR";
    static final String MOTECH_DB_INSTANCE = "MOTECH_DB_INSTANCE";
    static final String MOTECH_TENANT_ID = "MOTECH_TENANT_ID";
    private Environment environment;
    private ConfigFileReader configFileReader;
    //TODO: Move to properties file
    public static final String DEFAULT_CONFIG_LOCATION = "/etc/motech/config/bootstrap.properties";

    public BootstrapConfigLoaderImpl(ConfigFileReader configFileReader, Environment environment) {
        this.environment = environment;
        this.configFileReader = configFileReader;
    }

    @Override
    public BootstrapConfig getBootstrapConfig() {
        BootstrapConfig bootstrapConfig = new BootstrapConfig();
        String configLocation = environment.getValue(MOTECH_CONFIG_DIR);
        String dbHost;

        if (configLocation != null) {
            Properties properties = getPropertiesFromFile(configLocation, " specified by '" + MOTECH_CONFIG_DIR + "' environment variable.");
            dbHost = properties.getProperty("db.host");
        } else {
            dbHost = environment.getValue(MOTECH_DB_INSTANCE);
            if (dbHost == null) {
                Properties properties = getPropertiesFromFile(DEFAULT_CONFIG_LOCATION, " specified at " + DEFAULT_CONFIG_LOCATION);
                dbHost = properties.getProperty("db.host");
            }
        }

        bootstrapConfig.setDBHost(dbHost);
        return bootstrapConfig;

    }

    private Properties getPropertiesFromFile(String configLocation, String errorMessage) {
        try {
            return configFileReader.getProperties(new File(configLocation));
        } catch (IOException e) {
            MotechConfigurationException exception = new MotechConfigurationException("Error loading config file " + configLocation + errorMessage, e);
            throw exception;
        }
    }
}

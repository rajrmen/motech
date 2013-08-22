package org.motechproject.config.bootstrap.impl;

import org.motechproject.config.MotechConfigurationException;
import org.motechproject.config.bootstrap.BootstrapConfigLoader;
import org.motechproject.config.bootstrap.ConfigFileReader;
import org.motechproject.config.bootstrap.Environment;
import org.motechproject.config.domain.BootstrapConfig;
import org.motechproject.config.domain.ConfigSource;
import org.motechproject.config.domain.DBConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Default implementation of {@link BootstrapConfigLoader}.
 */
@Component
public class BootstrapConfigLoaderImpl implements BootstrapConfigLoader {

    private static final String BOOTSTRAP_PROPERTIES = "bootstrap.properties";
    private static final String DB_URL = "db.url";
    private static final String DB_USERNAME = "db.username";
    private static final String DB_PASSWORD = "db.password";
    private static final String TENANT_ID = "tenant.id";
    private static final String CONFIG_SOURCE = "config.source";

    private Environment environment;
    private ConfigFileReader configFileReader;

    @Value("${bootstrap.config.default.file.path:/etc/motech/config}")
    private String defaultBootstrapConfigLocation;

    @Autowired
    public BootstrapConfigLoaderImpl(ConfigFileReader configFileReader, Environment environment) {
        this.environment = environment;
        this.configFileReader = configFileReader;
    }

    String getDefaultBootstrapConfigLocation() {
        return defaultBootstrapConfigLocation;
    }

    void setDefaultBootstrapConfigLocation(String defaultBootstrapConfigLocation) {
        this.defaultBootstrapConfigLocation = defaultBootstrapConfigLocation;
    }

    @Override
    public BootstrapConfig loadBootstrapConfig() {
        String configLocation = environment.getConfigDir();

        if (configLocation != null) {
            final String errorMessage = "specified by '" + Environment.MOTECH_CONFIG_DIR + "' environment variable.";
            return readBootstrapConfigFromFile(getConfigFile(configLocation), errorMessage);
        }

        try {
            return readBootstrapConfigFromEnvironment();
        } catch (MotechConfigurationException e) {
            return readBootstrapConfigFromFile(getConfigFile(defaultBootstrapConfigLocation), "");
        }
    }

    private String getConfigFile(String configLocation) {
        return configLocation + "/" + BOOTSTRAP_PROPERTIES;
    }

    private BootstrapConfig readBootstrapConfigFromEnvironment() {
        String dbUrl = environment.getDBUrl();
        String username = environment.getDBUsername();
        String password = environment.getDBPassword();
        String tenantId = environment.getTenantId();
        String configSource = environment.getConfigSource();
        return new BootstrapConfig(new DBConfig(dbUrl, username, password), tenantId, ConfigSource.valueOf(configSource));
    }

    private BootstrapConfig readBootstrapConfigFromFile(String configFile, String errorMessage) {
        try {
            Properties properties = configFileReader.getProperties(new File(configFile));
            String dbUrl = properties.getProperty(DB_URL);
            String username = properties.getProperty(DB_USERNAME);
            String password = properties.getProperty(DB_PASSWORD);
            String tenantId = properties.getProperty(TENANT_ID);
            ConfigSource configSource = ConfigSource.valueOf(properties.getProperty(CONFIG_SOURCE));

            return new BootstrapConfig(new DBConfig(dbUrl, username, password), tenantId, configSource);

        } catch (IOException e) {
            throw new MotechConfigurationException("Error loading config file " + configFile + " " + errorMessage, e);
        }
    }
}

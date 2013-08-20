package org.motechproject.server.config.bootstrap.impl;

import org.motechproject.server.config.bootstrap.BootstrapConfigLoader;
import org.motechproject.server.config.bootstrap.ConfigFileReader;
import org.motechproject.server.config.bootstrap.Environment;
import org.motechproject.server.config.bootstrap.MotechConfigurationException;
import org.motechproject.server.config.domain.BootstrapConfig;
import org.motechproject.server.config.domain.ConfigSource;
import org.motechproject.server.config.domain.DBConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

@Component
public class BootstrapConfigLoaderImpl implements BootstrapConfigLoader {

    static final String BOOTSTRAP_PROPERTIES = "bootstrap.properties";
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
    public BootstrapConfig getBootstrapConfig() {
        String configLocation = environment.getValue(Environment.MOTECH_CONFIG_DIR);

        if (configLocation != null) {
            return readBootstrapConfigFromFile(configLocation + "/" + BOOTSTRAP_PROPERTIES, " specified by '" + Environment.MOTECH_CONFIG_DIR + "' environment variable.");
        }

        try {
            return readBootstrapConfigFromEnvironment();
        } catch (MotechConfigurationException e) {
            return readBootstrapConfigFromFile(defaultBootstrapConfigLocation + "/" + BOOTSTRAP_PROPERTIES, " specified at " + defaultBootstrapConfigLocation + "/" + BOOTSTRAP_PROPERTIES);
        }
    }

    private BootstrapConfig readBootstrapConfigFromEnvironment() {
        String dbUrl = environment.getValue(Environment.MOTECH_DB_URL);
        String username = environment.getValue(Environment.MOTECH_DB_USERNAME);
        String password = environment.getValue(Environment.MOTECH_DB_PASSWORD);
        String tenantId = environment.getValue(Environment.MOTECH_TENANT_ID);
        String configSource = environment.getValue(Environment.MOTECH_CONFIG_SOURCE);
        return new BootstrapConfig(new DBConfig(dbUrl, username, password), tenantId, ConfigSource.valueOf(configSource));
    }

    private BootstrapConfig readBootstrapConfigFromFile(String configFile, String errorMessage) {
        try {
            Properties properties = configFileReader.getProperties(new File(configFile));
            String dbUrl = properties.getProperty("db.url");
            String username = properties.getProperty("db.username");
            String password = properties.getProperty("db.password");
            String tenantId = properties.getProperty("tenant.id");
            ConfigSource configSource = ConfigSource.valueOf(properties.getProperty("config.source"));

            return new BootstrapConfig(new DBConfig(dbUrl, username, password), tenantId, configSource);

        } catch (IOException e) {
            throw new MotechConfigurationException("Error loading config file " + configFile + errorMessage, e);
        }
    }
}

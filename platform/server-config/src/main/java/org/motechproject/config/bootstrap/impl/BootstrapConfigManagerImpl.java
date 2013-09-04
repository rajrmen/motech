package org.motechproject.config.bootstrap.impl;

import org.apache.log4j.Logger;
import org.motechproject.config.MotechConfigurationException;
import org.motechproject.config.bootstrap.BootstrapConfigManager;
import org.motechproject.config.bootstrap.ConfigFileReader;
import org.motechproject.config.bootstrap.Environment;
import org.motechproject.config.bootstrap.mapper.BootstrapConfigPropertyMapper;
import org.motechproject.config.domain.BootstrapConfig;
import org.motechproject.config.domain.ConfigSource;
import org.motechproject.config.domain.DBConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 * Default implementation of {@link org.motechproject.config.bootstrap.BootstrapConfigManager}.
 */
@Component
public class BootstrapConfigManagerImpl implements BootstrapConfigManager {

    private static Logger logger = Logger.getLogger(BootstrapConfigManagerImpl.class);

    static final String BOOTSTRAP_PROPERTIES = "bootstrap.properties";
    static final String DEFAULT_BOOTSTRAP_CONFIG_DIR_PROP = "default.bootstrap.config.dir";
    static final String DEFAULT_BOOTSTRAP_CONFIG_DIR = "/etc/motech/config";

    private Environment environment;
    private ConfigFileReader configFileReader;
    private String defaultBootstrapConfigDir;

    @Autowired
    public BootstrapConfigManagerImpl(ConfigFileReader configFileReader, Environment environment, Properties configProperties) {
        this.environment = environment;
        this.configFileReader = configFileReader;
        final String defaultConfigDir = configProperties.getProperty(DEFAULT_BOOTSTRAP_CONFIG_DIR_PROP);
        this.defaultBootstrapConfigDir = defaultConfigDir != null ? defaultConfigDir : DEFAULT_BOOTSTRAP_CONFIG_DIR;
    }

    String getDefaultBootstrapConfigDir() {
        return defaultBootstrapConfigDir;
    }

    @Override
    public BootstrapConfig loadBootstrapConfig() {
        String configLocation = environment.getConfigDir();

        if (configLocation != null) {
            final String errorMessage = String.format("specified by '%s' environment variable.", Environment.MOTECH_CONFIG_DIR);
            return readBootstrapConfigFromFile(getConfigFile(configLocation), errorMessage);
        }

        try {
            return readBootstrapConfigFromEnvironment();
        } catch (MotechConfigurationException e) {
            logger.warn("Could not find bootstrap configuration values from environment variables. So, trying to load " +
                    "from default config file " + defaultBootstrapConfigDir, e);
            return readBootstrapConfigFromDefaultLocation();
        }
    }

    @Override
    public void saveBootstrapConfig(BootstrapConfig bootstrapConfig) {
        File bootstrapFile = getDefaultBootstrapFile();
        try {
            bootstrapFile.getParentFile().mkdirs();
            bootstrapFile.createNewFile();

            Properties bootstrapProperties = BootstrapConfigPropertyMapper.toProperties(bootstrapConfig);
            bootstrapProperties.store(new FileWriter(bootstrapFile), "bootstrap properties");
        } catch (IOException e) {
            logger.error("Error saving bootstrap properties to file" + e.getMessage());
            throw new MotechConfigurationException("Error saving bootstrap properties to file", e);
        }
    }

    File getDefaultBootstrapFile() {
        return new File(getDefaultBootstrapConfigDir(), BOOTSTRAP_PROPERTIES);
    }

    private BootstrapConfig readBootstrapConfigFromDefaultLocation() {
        BootstrapConfig bootstrapConfig = null;
        try {
            bootstrapConfig = readBootstrapConfigFromFile(getConfigFile(defaultBootstrapConfigDir), "");
        } catch (MotechConfigurationException ex) {
            logger.warn(ex.getMessage());
        }
        return bootstrapConfig;
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
            if (logger.isDebugEnabled()) {
                logger.debug("Trying to load bootstrap configuration from " + configFile);
            }

            Properties properties = configFileReader.getProperties(new File(configFile));
            return BootstrapConfigPropertyMapper.fromProperties(properties);

        } catch (IOException e) {
            final String message = "Error loading bootstrap properties from config file " + configFile + " " + errorMessage;
            throw new MotechConfigurationException(message, e);
        }
    }
}

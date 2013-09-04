package org.motechproject.config.bootstrap.mapper;

import org.motechproject.config.domain.BootstrapConfig;
import org.motechproject.config.domain.ConfigSource;
import org.motechproject.config.domain.DBConfig;

import java.util.Properties;

import static org.motechproject.config.domain.BootstrapConfig.CONFIG_SOURCE;
import static org.motechproject.config.domain.BootstrapConfig.DB_PASSWORD;
import static org.motechproject.config.domain.BootstrapConfig.DB_URL;
import static org.motechproject.config.domain.BootstrapConfig.DB_USERNAME;
import static org.motechproject.config.domain.BootstrapConfig.TENANT_ID;

public final class BootstrapConfigPropertyMapper {

    private BootstrapConfigPropertyMapper() {
    }


    public static Properties toProperties(BootstrapConfig bootstrapConfig) {
        Properties bootstrapProperties = new Properties();
        bootstrapProperties.setProperty(DB_URL, bootstrapConfig.getDbConfig().getUrl());
        bootstrapProperties.setProperty(DB_USERNAME, bootstrapConfig.getDbConfig().getUsername());
        bootstrapProperties.setProperty(DB_PASSWORD, bootstrapConfig.getDbConfig().getPassword());
        bootstrapProperties.setProperty(TENANT_ID, bootstrapConfig.getTenantId());
        bootstrapProperties.setProperty(CONFIG_SOURCE, bootstrapConfig.getConfigSource().getName());
        return bootstrapProperties;
    }

    public static BootstrapConfig fromProperties(Properties bootstrapProperties) {
        return new BootstrapConfig(new DBConfig(bootstrapProperties.getProperty(DB_URL),
                bootstrapProperties.getProperty(DB_USERNAME),
                bootstrapProperties.getProperty(DB_PASSWORD)),
                bootstrapProperties.getProperty(TENANT_ID),
                ConfigSource.valueOf(bootstrapProperties.getProperty(CONFIG_SOURCE)));
    }
}

package org.motechproject.server.config.service.impl;

import org.motechproject.server.config.domain.BundleConfig;
import org.motechproject.server.config.service.PlatformConfigurationService;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

@Service("platformConfigurationService")
public class PlatformConfigurationServiceImpl implements PlatformConfigurationService {

    private final Logger logger = LoggerFactory.getLogger(PlatformConfigurationService.class);

    private ConfigurationAdmin configurationAdmin;

    @Autowired
    public PlatformConfigurationServiceImpl(@Qualifier("configAdmin") ConfigurationAdmin configurationAdmin) {
        this.configurationAdmin = configurationAdmin;
    }

    @Override
    public BundleConfig get(String bundleSymbolicName) {
        BundleConfig bundleConfig = new BundleConfig();
        try {
            Configuration configuration = configurationAdmin.getConfiguration(bundleSymbolicName);

            Dictionary existingProperties = configuration.getProperties();
            if(existingProperties == null){
                return null;
            }
            Enumeration keys = existingProperties.keys();
            while (keys.hasMoreElements()) {
                Object key = keys.nextElement();
                bundleConfig.add(key.toString(), existingProperties.get(key).toString());
            }

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return bundleConfig;
    }

    @Override
    public void store(String bundleSymbolicName, BundleConfig bundleConfig) {
        try {
            Configuration configuration = configurationAdmin.getConfiguration(bundleSymbolicName);
            Dictionary current = hasProperties(configuration) ? configuration.getProperties() : new Properties();

            Map<String, String> updatedProperties = bundleConfig.properties();


            for (String key : updatedProperties.keySet()) {
                current.put(key, updatedProperties.get(key));
            }

            configuration.update(current);

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private boolean hasProperties(Configuration configuration) {
        Dictionary properties = configuration.getProperties();
        return properties != null && !properties.isEmpty();
    }


}

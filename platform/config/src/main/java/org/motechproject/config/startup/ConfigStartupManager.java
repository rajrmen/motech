package org.motechproject.config.startup;

import org.motechproject.commons.api.ApplicationContextServiceReferenceUtils;
import org.motechproject.config.domain.MotechConfig;
import org.motechproject.config.service.ConfigService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ConfigStartupManager extends ServiceTracker {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigStartupManager.class);
    private ConfigService configService;


    public ConfigStartupManager(BundleContext bundleContext, ConfigService configService) {
        super(bundleContext, ApplicationContext.class.getName(), null);
        this.configService = configService;
    }

    @Override
    public Object addingService(ServiceReference serviceReference) {
        Object service = super.addingService(serviceReference);
        if (ApplicationContextServiceReferenceUtils.isValid(serviceReference)) {
            String symbolicName = serviceReference.getBundle().getSymbolicName();
            Properties properties = getProperties(symbolicName);
            if (!properties.isEmpty()) {
                MotechConfig config = new MotechConfig(symbolicName, properties);
                LOGGER.debug("Persisting configuration in database. MotechConfig: " + config);
                configService.createConfig(config);
            }
        }
        return service;
    }

    public Properties getProperties(String symbolicName) {
        Properties properties = new Properties();
        String configDirPath = String.format("%s/.motech/config/%s", System.getProperty("user.home"), symbolicName);
        File configDir = new File(configDirPath);
        if (!configDir.exists()) {
            return properties;
        }
        List<File> propertyFiles = listPropertyFiles(configDir);
        try {
            for (File propertyFile : propertyFiles) {
                properties.load(new FileInputStream(propertyFile));
            }
        } catch (IOException e) {
            LOGGER.error("Error reading configs from path: " + configDirPath, e);
        }
        return properties;
    }

    private List<File> listPropertyFiles(File file) {
        List<File> propertyFiles = new ArrayList<>();
        if (file == null || !file.isDirectory()) {
            return propertyFiles;
        }
        File[] allFiles = file.listFiles();
        for (File f : allFiles) {
            if (f.isDirectory()) {
                propertyFiles.addAll(listPropertyFiles(f));
            } else {
                if (f.getName().endsWith(".properties")) {
                    propertyFiles.add(f);
                }
            }
        }
        return propertyFiles;
    }
}

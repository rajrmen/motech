package org.motechproject.config.startup;

import org.motechproject.commons.api.MotechFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class BundlePropertiesReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(BundlePropertiesReader.class);

    public BundlePropertiesReader() {
    }

    public Properties read(String bundleSymbolicName) {
        Properties properties = new Properties();
        String configDirPath = String.format("%s/.motech/config/%s", System.getProperty("user.home"), bundleSymbolicName);
        File configDir = new File(configDirPath);
        if (!configDir.exists()) {
            return properties;
        }
        List<File> propertyFiles = MotechFileUtils.recursivelyListAllPropertyFiles(configDir);
        try {
            for (File propertyFile : propertyFiles) {
                properties.load(new FileInputStream(propertyFile));
            }
        } catch (IOException e) {
            LOGGER.error("Error reading configs from path: " + configDirPath, e);
        }
        return properties;
    }
}
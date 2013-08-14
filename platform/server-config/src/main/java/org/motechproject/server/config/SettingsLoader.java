package org.motechproject.server.config;

import org.motechproject.commons.api.MotechException;
import org.motechproject.server.config.settings.PlatformSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class loads platform settings from files.
 */
@Component
public class SettingsLoader {
    public static final String MOTECH_SETTINGS_FILE = "motech-settings.conf";
    public static final String ACTIVEMQ_SETTINGS_FILE = "activemq.properties";
    public static final String SETTINGS_DEFAULT_DIR = "/etc/motech/config";

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsLoader.class);

    @Autowired
    private ResourceLoader resourceLoader;

    public PlatformSettings load(String directory) {
        Resource resource = resourceLoader.getResource(directory);
        return load(resource);
    }

    public PlatformSettings load(Resource location) {
        PlatformSettings platformSettings = null;

        try {
            Resource motechSettings = location.createRelative(MOTECH_SETTINGS_FILE);
            Resource activemqSettings = location.createRelative(ACTIVEMQ_SETTINGS_FILE);

            if (!motechSettings.isReadable()) {
                motechSettings = resourceLoader.getResource(SETTINGS_DEFAULT_DIR).createRelative(MOTECH_SETTINGS_FILE);

                if (!motechSettings.isReadable()) {
                    throw new IOException("Motech settings not found in default directory.");
                }
            }

            if (!activemqSettings.isReadable()) {
                activemqSettings = resourceLoader.getResource(SETTINGS_DEFAULT_DIR).createRelative(ACTIVEMQ_SETTINGS_FILE);

                if (!activemqSettings.isReadable()) {
                    throw new IOException("ActiveMQ settings not found in default directory.");
                }
            }

            platformSettings = loadSettingsFromStream(motechSettings, activemqSettings);

        } catch (IOException e) {
            LOGGER.warn("Problem reading properties files. Exception: " + e.getMessage());
        }

        return platformSettings;
    }

    private static PlatformSettings loadSettingsFromStream(Resource motechSettings, Resource activemqSettings) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");

            try (DigestInputStream dis1 = new DigestInputStream(motechSettings.getInputStream(), digest);
                 DigestInputStream dis2 = new DigestInputStream(activemqSettings.getInputStream(), digest)) {

                PlatformSettings platformSettings = new PlatformSettings();
                platformSettings.loadMotech(dis1);
                platformSettings.loadActivemq(dis2);
                platformSettings.setChecksum(digest.digest());

                return platformSettings;
            } catch (IOException e) {
                throw new MotechException("Error loading configuration", e);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new MotechException("MD5 algorithm not available", e);
        }
    }
}

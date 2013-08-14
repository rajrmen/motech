package org.motechproject.server.config.settings;

import org.motechproject.server.config.domain.MotechURL;
import org.motechproject.server.config.service.PlatformSettingsService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.DigestInputStream;
import java.util.Arrays;
import java.util.Properties;

public class ConfigFileSettings implements MotechSettings {

    private byte[] md5checkSum;
    private URL fileURL;
    private Properties motechSettings = new Properties();
    private Properties activemq = new Properties();

    public ConfigFileSettings() {
    }

    ConfigFileSettings(Properties motechSettings, Properties activemqSettings) {
        this.motechSettings.putAll(motechSettings);
        this.activemq.putAll(activemqSettings);
    }

    @Override
    public Properties getMotechProperties() {
        return motechSettings;
    }

    public String getLanguage() {
        return motechSettings.getProperty(MotechSettings.SYSTEM_LANGUAGE_PROP);
    }

    public String getStatusMsgTimeout() {
        return motechSettings.getProperty(MotechSettings.STATUS_MSG_TIMEOUT_PROP);
    }

    public String getLoginMode() {
        return motechSettings.getProperty(MotechSettings.LOGIN_MODE_PROP);
    }

    public String getProviderName() {
        return motechSettings.getProperty(MotechSettings.PROVIDER_NAME_PROP);
    }

    public String getProviderUrl() {
        return motechSettings.getProperty(MotechSettings.SCHEDULER_URL_PROP);
    }

    public String getServerUrl() {
        return new MotechURL(motechSettings.getProperty(MotechSettings.SERVER_URL_PROP)).toString();
    }


    public String getServerHost() {
        return new MotechURL(motechSettings.getProperty(MotechSettings.SERVER_URL_PROP)).getHost();
    }


    public String getUploadSize() {
        return motechSettings.getProperty(MotechSettings.UPLOAD_SIZE_PROP);
    }

    public byte[] getMd5checkSum() {
        return Arrays.copyOf(md5checkSum, md5checkSum.length);
    }

    public URL getFileURL() {
        return fileURL;
    }

    public void setFileURL(URL fileURL) {
        this.fileURL = fileURL;
    }

    public String getPath() {
        return getFileURL().getPath();
    }

    public synchronized void load(DigestInputStream inStream) throws IOException {
        motechSettings.load(inStream);
    }

    public synchronized void loadActiveMq(InputStream is) throws IOException {
        activemq.load(is);
    }

    @Override
    public Properties getActivemqProperties() {
        Properties activemqProperties = new Properties();
        activemqProperties.putAll(activemq);
        return activemqProperties;
    }

    public Properties getMetricsProperties() {
        Properties metricsProperties = new Properties();

        putPropertyIfNotNull(
                metricsProperties, MotechSettings.GRAPHITE_URL_PROP, motechSettings.getProperty(GRAPHITE_URL_PROP)
        );

        return metricsProperties;
    }

    public Properties getSchedulerProperties() {
        Properties schedulerProperties = new Properties();

        putPropertyIfNotNull(
                schedulerProperties, MotechSettings.SCHEDULER_URL_PROP, motechSettings.getProperty(SCHEDULER_URL_PROP)
        );

        return schedulerProperties;
    }

    private static void putPropertyIfNotNull(Properties properties, String key, Object value) {
        if (value != null) {
            properties.put(key, value);
        }
    }

    public void setMd5checksum(byte[] digest) {
        this.md5checkSum = Arrays.copyOf(digest, digest.length);
    }

    public void saveMotechSetting(String key, String value) {
        motechSettings.put(key, value);
    }

    public void storeMotechSettings() throws IOException {
        File file = new File(getPath() + File.separator + PlatformSettingsService.SETTINGS_FILE_NAME);
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            motechSettings.store(fileOutputStream, null);
        }
    }

    public Properties getAll() {
        Properties copy = new Properties();
        copy.putAll(motechSettings);
        copy.putAll(activemq);

        return copy;
    }

    public void saveActiveMqSetting(String key, String value) {
        activemq.put(key, value);
    }

    public void storeActiveMqSettings() throws IOException {
        File file = new File(getPath() + File.separator + PlatformSettingsService.ACTIVEMQ_FILE_NAME);
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            activemq.store(fileOutputStream, null);
        }


    }
}

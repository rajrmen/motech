package org.motechproject.server.config.domain;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.server.config.settings.MotechSettings;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

@TypeDiscriminator("doc.type === 'SettingsRecord'")
@JsonIgnoreProperties(ignoreUnknown = true, value = { "couchDbProperties" })
public class SettingsRecord extends MotechBaseDataObject implements MotechSettings {

    private String language;
    private String statusMsgTimeout;
    private String loginMode;
    private String providerName;
    private String providerUrl;
    private String serverUrl;
    private String uploadSize;

    private boolean cluster;
    private DateTime lastRun;
    private byte[] configFileChecksum = new byte[0];

    private Properties activemqProperties = new Properties();
    private Properties metricsProperties;
    private Properties schedulerProperties;
    private Properties motechProperties;

    @Override
    public Properties getMotechProperties() {
        motechProperties = new Properties();

        motechProperties.setProperty(MotechSettings.SYSTEM_LANGUAGE_PROP, language);
        motechProperties.setProperty(MotechSettings.STATUS_MSG_TIMEOUT_PROP, statusMsgTimeout);
        motechProperties.setProperty(MotechSettings.LOGIN_MODE_PROP, loginMode);
        motechProperties.setProperty(MotechSettings.PROVIDER_NAME_PROP, providerName);
        motechProperties.setProperty(MotechSettings.PROVIDER_URL_PROP, providerUrl);
        motechProperties.setProperty(MotechSettings.UPLOAD_SIZE_PROP, uploadSize);

        return  motechProperties;
    }

    public String getLanguage() {
        return language;
    }

    public String getStatusMsgTimeout() {
        return statusMsgTimeout;
    }

    public Properties getActivemqProperties() {
        return activemqProperties;
    }

    public Properties getMetricsProperties() {
        return metricsProperties;
    }

    public String getLoginMode() {
        return loginMode;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getProviderUrl() {
        return providerUrl;
    }

    public String getServerUrl() {
        return new MotechURL(this.serverUrl).toString();
    }

    public String getServerHost() {
        return new MotechURL(this.serverUrl).getHost();
    }

    public String getUploadSize() {
        return uploadSize;
    }

    public Properties getSchedulerProperties() {
        return schedulerProperties;
    }

    public void setActivemqProperties(final Properties activemqProperties) {
        this.activemqProperties = activemqProperties;
    }

    public void setMetricsProperties(final Properties metricsProperties) {
        this.metricsProperties = metricsProperties;
    }

    public void setSchedulerProperties(final Properties schedulerProperties) {
        this.schedulerProperties = schedulerProperties;
    }

    public void setLanguage(final String language) {
        this.language = language;
    }

    public void setLoginMode(String loginMode) {
        this.loginMode = loginMode;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public void setProviderUrl(String providerUrl) {
        this.providerUrl = providerUrl;
    }

    public void setStatusMsgTimeout(final String statusMsgTimeout) {
        this.statusMsgTimeout = statusMsgTimeout;
    }

    public boolean isCluster() {
        return cluster;
    }

    public void setCluster(final boolean cluster) {
        this.cluster = cluster;
    }

    public DateTime getLastRun() {
        return DateUtil.setTimeZoneUTC(lastRun);
    }

    public void setLastRun(final DateTime lastRun) {
        this.lastRun = lastRun;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public void setUploadSize(String uploadSize) {
        this.uploadSize = uploadSize;
    }

    public byte[] getConfigFileChecksum() {
        return Arrays.copyOf(configFileChecksum, configFileChecksum.length);
    }

    public void setConfigFileChecksum(final byte[] configFileChecksum) {
        this.configFileChecksum = Arrays.copyOf(configFileChecksum, configFileChecksum.length);
    }

    public void updateSettings(final MotechSettings settings) {
        setLanguage(settings.getMotechProperties().getProperty(MotechSettings.SYSTEM_LANGUAGE_PROP));
        setStatusMsgTimeout(settings.getMotechProperties().getProperty(MotechSettings.STATUS_MSG_TIMEOUT_PROP));
        setActivemqProperties(settings.getActivemqProperties());
        setLoginMode(settings.getMotechProperties().getProperty(MotechSettings.LOGIN_MODE_PROP));
        setProviderName(settings.getMotechProperties().getProperty(MotechSettings.PROVIDER_NAME_PROP));
        setProviderUrl(settings.getMotechProperties().getProperty(MotechSettings.PROVIDER_URL_PROP));
        setServerUrl(settings.getMotechProperties().getProperty(MotechSettings.SERVER_URL_PROP));
        setUploadSize(settings.getMotechProperties().getProperty(MotechSettings.UPLOAD_SIZE_PROP));

        Properties newMetricProperties = new Properties();
        newMetricProperties.put(
                MotechSettings.GRAPHITE_URL_PROP,
                settings.getMotechProperties().getProperty(MotechSettings.GRAPHITE_URL_PROP)
        );
        setMetricsProperties(newMetricProperties);

        Properties newSchedulerProperties = new Properties();
        newSchedulerProperties.put(
                MotechSettings.SCHEDULER_URL_PROP,
                settings.getMotechProperties().getProperty(MotechSettings.SCHEDULER_URL_PROP)
        );
        setSchedulerProperties(newSchedulerProperties);
    }

    public void updateFromProperties(final Properties props) {
        if (metricsProperties == null || metricsProperties.isEmpty()) {
            metricsProperties = emptyMetricsProperties();
        }

        if (schedulerProperties == null || schedulerProperties.isEmpty()) {
            schedulerProperties = emptySchedulerProperties();
        }

        handleMiscProperties(props);
    }

    private void handleMiscProperties(Properties props) {
        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();

            switch (key) {
                case MotechSettings.SYSTEM_LANGUAGE_PROP:
                    setLanguage(value);
                    break;
                case MotechSettings.STATUS_MSG_TIMEOUT_PROP:
                    setStatusMsgTimeout(value);
                    break;
                case MotechSettings.LOGIN_MODE_PROP:
                    setLoginMode(value);
                    break;
                case MotechSettings.PROVIDER_NAME_PROP:
                    setProviderName(value);
                    break;
                case MotechSettings.PROVIDER_URL_PROP:
                    setProviderUrl(value);
                    break;
                case MotechSettings.SERVER_URL_PROP:
                    setServerUrl(value);
                    break;
                case MotechSettings.UPLOAD_SIZE_PROP:
                    setUploadSize(value);
                    break;
                default:
                    handleMiscProperty(key, value);
                    break;
            }
        }
    }

    private void handleMiscProperty(String key, String value) {
        for (Properties p : Arrays.asList(getMetricsProperties(), getSchedulerProperties())) {
            if (p.containsKey(key)) {
                p.put(key, value);
                break;
            }
        }
    }

    private Properties emptyMetricsProperties() {
        Properties props = new Properties();
        props.put(MotechSettings.GRAPHITE_URL_PROP, "");
        return props;
    }

    private Properties emptySchedulerProperties() {
        Properties props = new Properties();
        props.put(MotechSettings.SCHEDULER_URL_PROP, "");
        return props;
    }
}

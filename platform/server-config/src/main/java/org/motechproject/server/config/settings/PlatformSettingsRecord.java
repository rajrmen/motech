package org.motechproject.server.config.settings;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.commons.api.MotechException;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Properties;

/**
 * Class represents platform settings stored in DB.
 */
@TypeDiscriminator("doc.type == 'PlatformSettingsRecord")
@JsonIgnoreProperties(ignoreUnknown = true, value = {"couchDbProperties"})
public class PlatformSettingsRecord extends MotechBaseDataObject implements MotechSettings {
    private Properties motechProperties;
    private Properties activemqProperties;
    private byte[] propertiesChecksum = new byte[0];
    private boolean isInitialized = false;

    private boolean cluster;
    private DateTime lastRun;

    @Override
    public Properties getActivemqProperties() {
        return activemqProperties;
    }

    @Override
    public Properties getMotechProperties() {
        return motechProperties;
    }

    public void setMotechProperties(Properties motechProperties) {
        this.motechProperties = motechProperties;
    }

    public void setActivemqProperties(Properties activemqProperties) {
        this.activemqProperties = activemqProperties;
        updateChecksum();
    }

    public byte[] getPropertiesChecksum() {
        return Arrays.copyOf(propertiesChecksum, propertiesChecksum.length);
    }

    public void setPropertiesChecksum(byte[] propertiesChecksum) {
        this.propertiesChecksum = Arrays.copyOf(propertiesChecksum, propertiesChecksum.length);
        updateChecksum();
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public void setInitialized(boolean initialized) {
        isInitialized = initialized;
    }

    public boolean isCluster() {
        return cluster;
    }

    public void setCluster(boolean cluster) {
        this.cluster = cluster;
    }

    public DateTime getLastRun() {
        return lastRun;
    }

    public void setLastRun(DateTime lastRun) {
        this.lastRun = lastRun;
    }

    public void updateSettings(final MotechSettings motechSettings) {
        this.setMotechProperties(motechSettings.getMotechProperties());
        this.setActivemqProperties(motechSettings.getActivemqProperties());
    }

    private void updateChecksum() throws MotechException {
        try {
            MessageDigest dig = MessageDigest.getInstance("MD5");
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Properties allProperties = new Properties(motechProperties);
            allProperties.putAll(activemqProperties);

            allProperties.store(outputStream,"");

            this.setPropertiesChecksum(dig.digest(outputStream.toByteArray()));
        } catch (Exception e) {
            throw new MotechException(e.getMessage(), e);
        }
    }
}

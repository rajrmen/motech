package org.motechproject.server.config.settings;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

/**
 * This class represents laoded platform settings.
 */
public class PlatformSettings implements MotechSettings {
    private Properties motechProperties;
    private Properties activemqProperties;
    private byte[] propertiesChecksum = new byte[0];

    @Override
    public Properties getActivemqProperties() {
        return activemqProperties;
    }

    @Override
    public Properties getMotechProperties() {
        return motechProperties;
    }

    public byte[] getChecksum() {
        return Arrays.copyOf(propertiesChecksum, propertiesChecksum.length);
    }

    public void setChecksum(byte[] checksum) {
        propertiesChecksum = Arrays.copyOf(checksum, checksum.length);
    }

    public void loadMotech(InputStream is) throws IOException {
        motechProperties.load(is);
    }

    public void loadActivemq(InputStream is) throws IOException {
        activemqProperties.load(is);
    }
}

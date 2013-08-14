package org.motechproject.server.config.service.impl;

import org.motechproject.server.config.service.ConfigFileReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigFileReaderImpl implements ConfigFileReader {

    @Override
    public Properties getProperties(File file) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(file));
        return properties;

    }
}

package org.motechproject.server.config.repository;

import org.apache.felix.cm.PersistenceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class ConfigurationStore implements PersistenceManager {

    private static Logger logger = LoggerFactory.getLogger(ConfigurationStore.class);

    private Map<String, Dictionary> configurations = new HashMap<>();

    @Override
    public boolean exists(String pid) {
        return configurations.containsKey(pid);
    }

    @Override
    public Dictionary load(String pid) throws IOException {
        return configurations.get(pid);
    }

    @Override
    public Enumeration getDictionaries() throws IOException {
        return Collections.enumeration(configurations.values());
    }

    @Override
    public void store(String pid, Dictionary properties) throws IOException {
        logger.info("Added configuration for pid " + pid);
        configurations.put(pid, properties);
    }

    @Override
    public void delete(String pid) throws IOException {
        configurations.remove(pid);
    }
}
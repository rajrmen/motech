package org.motechproject.server.config.test.domain;

import org.motechproject.server.config.annotations.ConfigUpdateListener;

import java.util.HashMap;
import java.util.Map;

public class TestConfigUpdater {


    private Map<String, String> existingProperties = new HashMap<>();


    @ConfigUpdateListener(pid = "foo")
    public void update(Map<String, String> properties) {
        System.out.println("Updater called");
        existingProperties.putAll(properties);
    }

    public Map<String, String> getExistingProperties() {
        return existingProperties;
    }
}

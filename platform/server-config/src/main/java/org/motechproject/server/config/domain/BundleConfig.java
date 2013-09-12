package org.motechproject.server.config.domain;

import java.util.HashMap;
import java.util.Map;

public class BundleConfig {

    private Map<String, String> properties = new HashMap<>();

    public Map<String, String> properties() {
        return properties;
    }

    public void add(String key, String value) {
        properties.put(key, value);
    }

    public String get(String key) {
        return properties.get(key);
    }
}

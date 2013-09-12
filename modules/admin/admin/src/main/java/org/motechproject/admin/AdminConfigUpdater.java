package org.motechproject.admin;

import org.motechproject.server.config.annotations.ConfigUpdateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


public class AdminConfigUpdater {

    private static Logger logger = LoggerFactory.getLogger(AdminConfigUpdater.class);

    @ConfigUpdateListener(pid = "test")
    public void updated(Map<String, String> properties) {
        for (String key : properties.keySet()) {
            logger.error(String.format("Updated %s = %s ", key, properties.get(key)));
        }
    }

}

package org.motechproject.admin;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class BundleConfig implements ManagedService {


    private static final Logger LOGGER = LoggerFactory.getLogger(BundleConfig.class);
    private Map<String, String> configuration = new HashMap<>();

    @Override
    public void updated(Dictionary properties) throws ConfigurationException {

        Enumeration keys = properties.keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = properties.get(key);
            configuration.put(key.toString(), value.toString());
            String message = String.format(" @@@ %s = %s @@@ ", key, value);
            LOGGER.info(message);
        }

    }

}

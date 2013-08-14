package org.motechproject.server.config.service;

public class MotechConfigurationException extends RuntimeException {
    public MotechConfigurationException(String message, Exception exception) {
        super(message, exception);
    }
}

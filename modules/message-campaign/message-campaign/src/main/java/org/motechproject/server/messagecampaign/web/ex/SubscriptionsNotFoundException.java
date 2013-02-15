package org.motechproject.server.messagecampaign.web.ex;


public class SubscriptionsNotFoundException extends RuntimeException {

    public SubscriptionsNotFoundException() {
    }

    public SubscriptionsNotFoundException(String message) {
        super(message);
    }

    public SubscriptionsNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

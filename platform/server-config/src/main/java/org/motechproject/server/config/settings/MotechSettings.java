package org.motechproject.server.config.settings;

import java.util.Properties;

public interface MotechSettings {
    // these are going to bootstrap.config
    String GRAPHITE_URL_PROP = "graphite.url";
    String SCHEDULER_URL_PROP = "scheduler.url";
    String SYSTEM_LANGUAGE_PROP = "system.language";
    String STATUS_MSG_TIMEOUT_PROP = "statusmsg.timeout";
    String PROVIDER_NAME_PROP = "provider.name";
    String PROVIDER_URL_PROP = "provider.url";
    String LOGIN_MODE_PROP = "login.mode";
    // end

    String SERVER_URL_PROP = "server.url";
    String UPLOAD_SIZE_PROP = "upload.size";

    String QUEUE_FOR_EVENTS_PROP = "queue.for.events";
    String QUEUE_FOR_SCHEDULER_PROP = "queue.for.scheduler";
    String BROKER_URL_PROP = "broker.url";
    String MAXIMUM_REDELIVERIES_PROP = "maximumRedeliveries";
    String REDELIVERY_DELAY_IN_MILIS_PROP = "redeliveryDelayInMillis";
    String CONCURRENT_CONSUMERS_PROP = "concurrentConsumers";
    String JMS_SESSION_CACHE_SIZE_PROP = "jms.session.cache.size";
    String JMS_CACHE_PRODUCERS_PROP = "jms.cache.producers";
    String JMS_USERNAME_PROP = "jms.username";
    String JMS_PASSWORD = "jms.password";

    Properties getActivemqProperties();

    Properties getMotechProperties();
}

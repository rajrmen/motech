package org.motechproject.scheduler;

import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.MotechEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * \ingroup scheduler
 *
 * Main class that can bootstrap a Motech Scheduler
 *
 * @author Igor (iopushnyev@2paths.com)
 */
public class MotechScheduler {
    private final static Logger log = LoggerFactory.getLogger(MotechSchedulerServiceImpl.class);
    public static final String CONFIG_LOCATION = "/applicationPlatformScheduler.xml";

    private final static String SCHEDULE_TEST_INPUT_PARAM = "-t";
    private final static String UNSCHEDULE_TEST_INPUT_PARAM = "-c";

    private final static String EVENT_MESSAGE_INPUT_PARAM = "-e";

    private final static String SUBJECT = "-s";

    private final static String TEST_EVENT_NAME = "testEvent";
    private static final String TEST_SUBJECT = "test";

    @Autowired
    private MotechSchedulerService schedulerService;

    public static void main(String[] args) {
        AbstractApplicationContext ctx = new ClassPathXmlApplicationContext(CONFIG_LOCATION);

        // add a shutdown hook for the above context...
        ctx.registerShutdownHook();

        log.info("Motech Scheduler started...");

        try {
            if (args.length > 0) {
                MotechScheduler motechScheduler = ctx.getBean(MotechScheduler.class);

                if (SCHEDULE_TEST_INPUT_PARAM.equals(args[0])) {
                    motechScheduler.scheduleTestEvent();
                } else if (UNSCHEDULE_TEST_INPUT_PARAM.equals(args[0])) {
                    motechScheduler.unscheduleTestEvent();
                } else if (EVENT_MESSAGE_INPUT_PARAM.equals(args[0])) {
                    Map<String, String> map = motechScheduler.getParams(args);

                    if (map.containsKey(SUBJECT)) {
                        motechScheduler.sendEventMessage(map.get(SUBJECT), null);
                    } else {
                        log.info(String.format("Usage: java MotechScheduler %s %s", EVENT_MESSAGE_INPUT_PARAM, SUBJECT));
                    }
                } else {
                    log.warn(String.format("Unknown parameter: %s - ignored", args[0]));
                }
            }
        } catch (Exception e) {
            log.error("Error: ", e);
        }
    }

    public void sendEventMessage(String subject, Map<String, Object> parameters) {
        ApplicationContext context = new ClassPathXmlApplicationContext(CONFIG_LOCATION);
        SchedulerFireEventGateway gateway = context.getBean(SchedulerFireEventGateway.class);
        MotechEvent event = new MotechEvent(subject, parameters);

        gateway.sendEventMessage(event);

        log.info(String.format("Sending Motech Event Message: %s", event));
    }

    private void scheduleTestEvent() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(MotechSchedulerService.JOB_ID_KEY, TEST_EVENT_NAME);
        MotechEvent motechEvent = new MotechEvent(TEST_SUBJECT, params);
        CronSchedulableJob cronSchedulableJob = new CronSchedulableJob(motechEvent, "0/5 * * * * ?");

        try {
            log.info(String.format("Scheduling test job: %s", cronSchedulableJob));
            schedulerService.scheduleJob(cronSchedulableJob);
        } catch (Exception e) {
            log.warn(String.format("Can not schedule test job. %s", e.getMessage()));
        }
    }

    private void unscheduleTestEvent() {
        try {
            log.info(String.format("Unscheduling the test job: %s", TEST_EVENT_NAME));
            schedulerService.unscheduleJob(TEST_SUBJECT, TEST_EVENT_NAME);
        } catch (Exception e) {
            log.warn(String.format("Can not unschedule the test job: %s %s", TEST_EVENT_NAME, e.getMessage()));
        }
    }

    private Map<String, String> getParams(final String[] args) {
        Map<String, String> params = new HashMap<String, String>(args.length - 1);

        for (int i = 1; i < args.length; i += 2) {
            params.put(args[i], args[i + 1]);
        }

        return params;
    }

}

package org.motechproject.scheduler;

import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.MotechEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    private final static String STANDALONE_PROCESS_INPUT_PARAM = "-s";

    private final static String TEST_EVENT_NAME = "testEvent";
    private static final String SUBJECT = "test";
    private static final String CRON_EXPRESSION = "0/5 * * * * ?";

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
                } else if (STANDALONE_PROCESS_INPUT_PARAM.equals(args[0])) {
                    String subject = motechScheduler.getEventSubject();

                    motechScheduler.sendEventMessage(subject, null);
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
        MotechEvent motechEvent = new MotechEvent(SUBJECT, params);
        CronSchedulableJob cronSchedulableJob = new CronSchedulableJob(motechEvent, CRON_EXPRESSION);

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
            schedulerService.unscheduleJob(SUBJECT, TEST_EVENT_NAME);
        } catch (Exception e) {
            log.warn(String.format("Can not unschedule the test job: %s %s", TEST_EVENT_NAME, e.getMessage()));
        }
    }

    private String getEventSubject() {
        String subject = "";

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            do {
                System.out.print("Please enter motech event subject: ");
                subject = reader.readLine();
            } while (subject == null || subject.equalsIgnoreCase(""));
        } catch (IOException e) {
            log.error("Error: ", e);
        }

        return subject;
    }

}

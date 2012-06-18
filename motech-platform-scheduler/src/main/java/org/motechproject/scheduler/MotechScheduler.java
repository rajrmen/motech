package org.motechproject.scheduler;

import com.google.gson.reflect.TypeToken;
import org.motechproject.dao.MotechJsonReader;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.RepeatingSchedulableJob;
import org.motechproject.model.RunOnceSchedulableJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.motechproject.util.DateUtil.date;

/**
 * \ingroup scheduler
 *
 * Main class that can bootstrap a Motech Scheduler
 *
 * @author Igor (iopushnyev@2paths.com)
 */
public class MotechScheduler {
    private final static Logger log = LoggerFactory.getLogger(MotechSchedulerServiceImpl.class);
    private static final String CONFIG_LOCATION = "/applicationPlatformScheduler.xml";

    private final static String SCHEDULE_TEST_INPUT_PARAM = "-st";
    private final static String UNSCHEDULE_TEST_INPUT_PARAM = "-ust";

    private final static String EVENT_MESSAGE_INPUT_PARAM = "-e";
    private final static String CRON_SCHEDULABLE_JOB_INPUT_PARAM = "-csj";
    private final static String REPEATING_SCHEDULABLE_JOB_INPUT_PARAM = "-rsj";
    private final static String RUN_ONCE_SCHEDULABLE_JOB_INPUT_PARAM = "-rosj";

    private final static String SUBJECT = "-s";
    private final static String PARAMETERS = "-p";
    private final static String CRON_EXPRESSION = "-ce";
    private final static String START_DATE = "-sd";
    private final static String END_DATE = "-ed";
    private final static String REPEAT_COUNT = "-rc";
    private final static String REPEAT_INTERVAL = "-ri";

    private final static String TEST_EVENT_NAME = "testEvent";
    private static final String TEST_SUBJECT = "test";
    private static final String TEST_CRON_EXPRESSION = "0/5 * * * * ?";

    @Autowired
    private MotechSchedulerService schedulerService;

    @Autowired
    private SchedulerFireEventGateway schedulerFireEventGateway;

    public static void main(final String[] args) {
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
                    motechScheduler.sendEventMessage(convertArguments(args));
                } else if (CRON_SCHEDULABLE_JOB_INPUT_PARAM.equals(args[0])) {
                    motechScheduler.scheduleCronSchedulableJob(convertArguments(args));
                } else if (REPEATING_SCHEDULABLE_JOB_INPUT_PARAM.equals(args[0])) {
                    motechScheduler.scheduleRepeatingSchedulableJob(convertArguments(args));
                } else if (RUN_ONCE_SCHEDULABLE_JOB_INPUT_PARAM.equals(args[0])) {
                    motechScheduler.scheduleRunOnceSchedulableJob(convertArguments(args));
                } else {
                    log.warn(String.format("Unknown parameter: %s - ignored", args[0]));
                }
            }
        } catch (Exception e) {
            log.error("Error: ", e);
        }
    }

    private static Map<String, String> convertArguments(final String[] args) {
        Map<String, String> params = new HashMap<String, String>(args.length - 1);

        for (int i = 1; i < args.length; i += 2) {
            params.put(args[i], args[i + 1]);
        }

        return params;
    }

    private void sendEventMessage(final Map<String, String> map) {
        if (map.containsKey(SUBJECT)) {
            MotechEvent event = createMotechEvent(map);
            schedulerFireEventGateway.sendEventMessage(createMotechEvent(map));

            log.info(String.format("Sending Motech Event Message: %s", event));
        } else {
            log.info(String.format("Usage: java MotechScheduler %s %s [%s]",
                    EVENT_MESSAGE_INPUT_PARAM, SUBJECT, PARAMETERS));
        }
    }

    private void scheduleCronSchedulableJob(final Map<String, String> map) {
        Date startTime = map.containsKey(START_DATE) ? date(map.get(START_DATE)) : null;
        Date endTime = map.containsKey(END_DATE) ? date(map.get(END_DATE)) : null;

        if (map.containsKey(SUBJECT) && map.containsKey(CRON_EXPRESSION)) {
            CronSchedulableJob cronSchedulableJob = new CronSchedulableJob(createMotechEvent(map),
                    map.get(CRON_EXPRESSION), startTime, endTime);

            try {
                log.info(String.format("Scheduling job: %s", cronSchedulableJob));
                schedulerService.safeScheduleJob(cronSchedulableJob);
            } catch (Exception e) {
                log.warn(String.format("Can not schedule job. %s", e.getMessage()));
            }
        } else {
            log.info(String.format("Usage: java MotechScheduler %s %s [%s] %s [%s] [%s]",
                    CRON_SCHEDULABLE_JOB_INPUT_PARAM, SUBJECT, PARAMETERS, CRON_EXPRESSION, START_DATE, END_DATE));
        }
    }

    private void scheduleRepeatingSchedulableJob(final Map<String, String> map) {
        Date endTime = map.containsKey(END_DATE) ? date(map.get(END_DATE)) : null;
        Integer repeatCount = map.containsKey(REPEAT_COUNT) ? Integer.valueOf(map.get(REPEAT_COUNT)) : null;
        Long repeatInterval = map.containsKey(REPEAT_INTERVAL) ? Long.valueOf(map.get(REPEAT_INTERVAL)) : null;

        if (map.containsKey(SUBJECT) && map.containsKey(START_DATE) && (repeatCount != null || repeatInterval != null)) {
            RepeatingSchedulableJob repeatingSchedulableJob = new RepeatingSchedulableJob(createMotechEvent(map),
                    date(map.get(START_DATE)), endTime, repeatCount, repeatInterval);

            try {
                log.info(String.format("Scheduling job: %s", repeatingSchedulableJob));
                schedulerService.safeScheduleRepeatingJob(repeatingSchedulableJob);
            } catch (Exception e) {
                log.warn(String.format("Can not schedule job. %s", e.getMessage()));
            }
        } else {
            log.info(String.format("Usage: java MotechScheduler %s %s [%s] %s [%s] [%s] %s",
                    REPEATING_SCHEDULABLE_JOB_INPUT_PARAM, SUBJECT, PARAMETERS, START_DATE, END_DATE, REPEAT_COUNT,
                    REPEAT_INTERVAL));
        }
    }

    private void scheduleRunOnceSchedulableJob(final Map<String, String> map) {
        Date startDate = map.containsKey(START_DATE) ? date(map.get(START_DATE)) : null;

        if (map.containsKey(SUBJECT) && startDate != null) {
            RunOnceSchedulableJob runOnceSchedulableJob = new RunOnceSchedulableJob(createMotechEvent(map), startDate);

            try {
                log.info(String.format("Scheduling job: %s", runOnceSchedulableJob));
                schedulerService.safeScheduleRunOnceJob(runOnceSchedulableJob);
            } catch (Exception e) {
                log.warn(String.format("Can not schedule job. %s", e.getMessage()));
            }
        } else {
            log.info(String.format("Usage: java MotechScheduler %s %s [%s] %s ", CRON_SCHEDULABLE_JOB_INPUT_PARAM,
                    SUBJECT, PARAMETERS, START_DATE));
        }
    }

    private void scheduleTestEvent() {
        Map<String, String> map = new HashMap<String, String>(3);
        map.put(SUBJECT, TEST_SUBJECT);
        map.put(PARAMETERS, String.format("{ %s: '%s' }", MotechSchedulerService.JOB_ID_KEY, TEST_EVENT_NAME));
        map.put(CRON_EXPRESSION, TEST_CRON_EXPRESSION);

        scheduleCronSchedulableJob(map);
    }

    private void unscheduleTestEvent() {
        try {
            log.info(String.format("Unscheduling the test job: %s", TEST_EVENT_NAME));
            schedulerService.unscheduleJob(TEST_SUBJECT, TEST_EVENT_NAME);
        } catch (Exception e) {
            log.warn(String.format("Can not unschedule the test job: %s %s", TEST_EVENT_NAME, e.getMessage()));
        }
    }

    private Map<String, Object> getEventParameters(final String parametersAsJSON) {
        Map<String, Object> map = new HashMap<String, Object>();
        MotechJsonReader reader = new MotechJsonReader();

        Object obj = reader.readFromString(parametersAsJSON, new TypeToken<Map<String, String>>(){ }.getType());

        if (obj != null) {
            map.putAll((Map<String, Object>) obj);
        } else {
            map = null;
        }

        return map;
    }

    private MotechEvent createMotechEvent(final Map<String, String> map) {
        MotechEvent event = null;

        if (map.containsKey(SUBJECT) && map.containsKey(PARAMETERS)) {
            event = new MotechEvent(map.get(SUBJECT), getEventParameters(map.get(PARAMETERS)));
        } else if (map.containsKey(SUBJECT)) {
            event = new MotechEvent(map.get(SUBJECT));
        }

        return event;
    }
}

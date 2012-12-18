package org.motechproject.scheduler.quartz;

import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.scheduler.impl.MotechScheduledJob;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.motechproject.commons.date.util.DateUtil.now;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testSchedulerApplicationContext.xml")
public class CouchdbStoreSchedulingIT {

    @Autowired
    private CouchdbStore couchdbStore;
    @Autowired
    private CouchdbJobStore jobStore;
    @Autowired
    private CouchdbTriggerStore triggerStore;

    @Autowired
    @Qualifier("schedulerConnector")
    CouchDbConnector db;

    @Autowired
    @Qualifier("quartzCouchdbProperties")
    Properties properties;

    @Autowired
    @Qualifier("couchdbSchedulerFactory")
    SchedulerFactoryBean schedulerFactoryBean;

    @Test
    public void shouldScheduleAndFireJob() throws SchedulerException, InterruptedException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();

        JobDetail job = newJob(MotechScheduledJob.class)
            .withIdentity("fooid", "bargroup")
            .usingJobData("foo", "bar")
            .usingJobData("fuu", "baz")
            .ofType(JobListener.class)
            .build();

        SimpleTriggerImpl trigger = (SimpleTriggerImpl) newTrigger()
            .withIdentity("fuuid1", "borgroup1")
            .forJob(JobKey.jobKey("fooid", "bargroup"))
            .startAt(now().toDate())
            .withSchedule(simpleSchedule()
                .withIntervalInSeconds(10)
                .withRepeatCount(2))
            .build();

        scheduler.scheduleJob(job, trigger);

        JobListener listner = JobListener.getInstance();
        synchronized (listner.getLock()) {
            listner.getLock().wait();
        }
        assertEquals(3, listner.getFireTimes().size());
    }

    @After
    public void teardown() {
        try {
            for (Object doc : jobStore.getAll())
                db.delete(doc);
        } catch (Exception e) {
        }
        try {
            for (Object doc : triggerStore.getAll())
                db.delete(doc);
        } catch (Exception e) {
        }
    }
}

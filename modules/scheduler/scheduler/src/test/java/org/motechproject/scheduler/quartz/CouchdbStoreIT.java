package org.motechproject.scheduler.quartz;

import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.scheduler.impl.MotechScheduledJob;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.JobPersistenceException;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.quartz.spi.OperableTrigger;
import org.quartz.spi.TriggerFiredResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.motechproject.commons.date.util.DateUtil.newDate;
import static org.motechproject.commons.date.util.DateUtil.newDateTime;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testSchedulerApplicationContext.xml")
public class CouchdbStoreIT {

    @Autowired
    private CouchdbStore couchdbStore;
    @Autowired
    private CouchdbJobStore jobStore;
    @Autowired
    private CouchdbTriggerStore triggerStore;

    @Autowired
    @Qualifier("schedulerConnector")
    CouchDbConnector db;

    @Test
    public void shouldStoreAndRetrieveJob() throws JobPersistenceException {
        JobDetail job = newJob(MotechScheduledJob.class)
            .withIdentity("fooid", "bargroup")
            .usingJobData("foo", "bar")
            .usingJobData("fuu", "baz")
            .build();
        couchdbStore.storeJob(job, false);

        assertNull(couchdbStore.retrieveJob(JobKey.jobKey("something", "something")));
        assertEquals("bar", couchdbStore.retrieveJob(JobKey.jobKey("fooid", "bargroup")).getJobDataMap().get("foo"));
    }

    @Test
    public void shouldUpdateExistingJob() throws JobPersistenceException {
        JobDetail job = newJob(MotechScheduledJob.class)
            .withIdentity("fooid", "bargroup")
            .usingJobData("foo", "bar")
            .build();
        couchdbStore.storeJob(job, false);

        JobDetail newJob = newJob(MotechScheduledJob.class)
            .withIdentity("fooid", "bargroup")
            .usingJobData("fii", "bur")
            .build();
        couchdbStore.storeJob(newJob, true);

        assertEquals("bur", couchdbStore.retrieveJob(JobKey.jobKey("fooid", "bargroup")).getJobDataMap().get("fii"));
    }

    @Test(expected = ObjectAlreadyExistsException.class)
    public void shouldNotUpdateExistingJob() throws JobPersistenceException {
        JobDetail job = newJob(MotechScheduledJob.class)
            .withIdentity("fooid", "bargroup")
            .usingJobData("foo", "bar")
            .build();
        couchdbStore.storeJob(job, false);

        JobDetail newJob = newJob(MotechScheduledJob.class)
            .withIdentity("fooid", "bargroup")
            .usingJobData("fii", "bur")
            .build();
        couchdbStore.storeJob(newJob, false);

        assertNull(couchdbStore.retrieveJob(JobKey.jobKey("fooid", "bargroup")).getJobDataMap().get("fii"));
    }

    @Test
    public void shouldDeleteExistingJob() throws JobPersistenceException {
        JobDetail job = newJob(MotechScheduledJob.class)
            .withIdentity("fooid", "bargroup")
            .usingJobData("foo", "bar")
            .build();
        couchdbStore.storeJob(job, false);

        couchdbStore.removeJob(JobKey.jobKey("fooid", "bargroup"));
        assertNull(couchdbStore.retrieveJob(JobKey.jobKey("fooid", "bargroup")));
    }

    @Test
    public void shouldDeleteAssociatedTriggersWhenDeletingJob() throws JobPersistenceException {
        JobDetail job = newJob(MotechScheduledJob.class)
            .withIdentity("fooid", "bargroup")
            .build();
        couchdbStore.storeJob(job, false);

        SimpleTriggerImpl trigger = (SimpleTriggerImpl) newTrigger()
            .withIdentity("fuuid", "borgroup")
            .forJob(JobKey.jobKey("fooid", "bargroup"))
            .startAt(new Date(2010, 10, 20))
            .withSchedule(simpleSchedule()
                .withIntervalInMinutes(2)
                .repeatForever())
            .build();
        couchdbStore.storeTrigger(trigger, false);

        couchdbStore.removeJob(JobKey.jobKey("fooid", "bargroup"));
        assertNull(couchdbStore.retrieveTrigger(TriggerKey.triggerKey("fuuid", "borgroup")));
    }

    @Test
    public void shouldDeleteExistingJobs() throws JobPersistenceException {
        JobDetail job1 = newJob(MotechScheduledJob.class)
            .withIdentity("fooid1", "bargroup1")
            .build();
        JobDetail job2 = newJob(MotechScheduledJob.class)
            .withIdentity("fooid2", "bargroup2")
            .build();
        JobDetail job3 = newJob(MotechScheduledJob.class)
            .withIdentity("fooid3", "bargroup2")
            .build();

        couchdbStore.storeJob(job1, false);
        couchdbStore.storeJob(job2, false);
        couchdbStore.storeJob(job3, false);

        couchdbStore.removeJobs(asList(
            JobKey.jobKey("fooid1", "bargroup1"),
            JobKey.jobKey("fooid2", "bargroup2")
        ));

        assertNull(couchdbStore.retrieveJob(JobKey.jobKey("fooid1", "bargroup1")));
        assertNull(couchdbStore.retrieveJob(JobKey.jobKey("fooid2", "bargroup2")));
        assertNotNull(couchdbStore.retrieveJob(JobKey.jobKey("fooid3", "bargroup2")));
    }

    @Test
    public void shouldCheckWhetherJobExists() throws JobPersistenceException {
        JobDetail job = newJob(MotechScheduledJob.class)
            .withIdentity("fooid", "bargroup")
            .build();

        assertFalse(couchdbStore.checkExists(JobKey.jobKey("fooid", "bargroup")));
        couchdbStore.storeJob(job, false);
        assertTrue(couchdbStore.checkExists(JobKey.jobKey("fooid", "bargroup")));
    }

    @Test
    public void shouldCountAllJobs() throws JobPersistenceException {
        JobDetail job1 = newJob(MotechScheduledJob.class)
            .withIdentity("fooid1", "bargroup1")
            .build();
        JobDetail job2 = newJob(MotechScheduledJob.class)
            .withIdentity("fooid2", "bargroup2")
            .build();
        JobDetail job3 = newJob(MotechScheduledJob.class)
            .withIdentity("fooid3", "bargroup2")
            .build();

        couchdbStore.storeJob(job1, false);
        couchdbStore.storeJob(job2, false);
        couchdbStore.storeJob(job3, false);

        assertEquals(3, couchdbStore.getNumberOfJobs());
    }

    @Test
    public void shouldReturnMatchingJobKeys() throws JobPersistenceException {
        JobDetail job1 = newJob(MotechScheduledJob.class)
            .withIdentity("fooid1", "bargroup1")
            .build();
        JobDetail job2 = newJob(MotechScheduledJob.class)
            .withIdentity("fooid2", "bargroup2")
            .build();
        JobDetail job3 = newJob(MotechScheduledJob.class)
            .withIdentity("fooid3", "bargroup2")
            .build();
        couchdbStore.storeJob(job1, false);
        couchdbStore.storeJob(job2, false);
        couchdbStore.storeJob(job3, false);

        assertEquals(
            new HashSet<>(asList(JobKey.jobKey("fooid2", "bargroup2"), JobKey.jobKey("fooid3", "bargroup2"))),
            couchdbStore.getJobKeys(GroupMatcher.<JobKey>groupEquals("bargroup2")));
    }

    @Test
    public void shouldReturnAllJobGroupNames() throws JobPersistenceException {
        JobDetail job1 = newJob(MotechScheduledJob.class)
            .withIdentity("fooid1", "bargroup1")
            .build();
        JobDetail job2 = newJob(MotechScheduledJob.class)
            .withIdentity("fooid2", "bargroup2")
            .build();
        JobDetail job3 = newJob(MotechScheduledJob.class)
            .withIdentity("fooid3", "bargroup2")
            .build();
        couchdbStore.storeJob(job1, false);
        couchdbStore.storeJob(job2, false);
        couchdbStore.storeJob(job3, false);

        assertEquals(asList("bargroup1" ,"bargroup2"), couchdbStore.getJobGroupNames());
    }

    @Test
    public void shouldStoreAndRetrieveTrigger() throws JobPersistenceException {
        SimpleTriggerImpl trigger = (SimpleTriggerImpl) newTrigger()
            .withIdentity("fuuid", "borgroup")
            .forJob(JobKey.jobKey("fooid", "bargroup"))
            .startAt(new Date(2010, 10, 20))
            .withSchedule(simpleSchedule()
                .withIntervalInMinutes(2)
                .repeatForever())
            .build();
        couchdbStore.storeTrigger(trigger, false);

        assertNull(couchdbStore.retrieveTrigger(TriggerKey.triggerKey("something", "something")));
        assertEquals(new Date(2010, 10, 20), couchdbStore.retrieveTrigger(TriggerKey.triggerKey("fuuid", "borgroup")).getStartTime());
    }

    @Test
    public void shouldUpdateExistingTrigger() throws JobPersistenceException {
        SimpleTriggerImpl trigger = (SimpleTriggerImpl) newTrigger()
            .withIdentity("fuuid", "borgroup")
            .forJob(JobKey.jobKey("fooid", "bargroup"))
            .startAt(new Date(2010, 10, 20))
            .withSchedule(simpleSchedule()
                .withIntervalInMinutes(2)
                .repeatForever())
            .build();
        couchdbStore.storeTrigger(trigger, false);

        SimpleTriggerImpl newTrigger = (SimpleTriggerImpl) newTrigger()
            .withIdentity("fuuid", "borgroup")
            .forJob(JobKey.jobKey("fooid", "bargroup"))
            .startAt(new Date(2012, 10, 20))
            .withSchedule(simpleSchedule()
                .withIntervalInMinutes(2)
                .repeatForever())
            .build();
        couchdbStore.storeTrigger(newTrigger, true);

        assertEquals(new Date(2012, 10, 20), couchdbStore.retrieveTrigger(TriggerKey.triggerKey("fuuid", "borgroup")).getStartTime());
    }

    @Test(expected = ObjectAlreadyExistsException.class)
    public void shouldNotUpdateExistingTrigger() throws JobPersistenceException {
        SimpleTriggerImpl trigger = (SimpleTriggerImpl) newTrigger()
            .withIdentity("fuuid", "borgroup")
            .forJob(JobKey.jobKey("fooid", "bargroup"))
            .startAt(new Date(2010, 10, 20))
            .withSchedule(simpleSchedule()
                .withIntervalInMinutes(2)
                .repeatForever())
            .build();
        couchdbStore.storeTrigger(trigger, false);

        SimpleTriggerImpl newTrigger = (SimpleTriggerImpl) newTrigger()
            .withIdentity("fuuid", "borgroup")
            .forJob(JobKey.jobKey("fooid", "bargroup"))
            .startAt(new Date(2010, 10, 20))
            .withSchedule(simpleSchedule()
                .withIntervalInMinutes(2)
                .repeatForever())
            .build();
        couchdbStore.storeTrigger(newTrigger, false);
    }

    @Test
    public void shouldDeleteExistingTrigger() throws JobPersistenceException {
        SimpleTriggerImpl trigger = (SimpleTriggerImpl) newTrigger()
            .withIdentity("fuuid", "borgroup")
            .forJob(JobKey.jobKey("fooid", "bargroup"))
            .startAt(new Date(2010, 10, 20))
            .withSchedule(simpleSchedule()
                .withIntervalInMinutes(2)
                .repeatForever())
            .build();
        couchdbStore.storeTrigger(trigger, false);

        couchdbStore.removeTrigger(TriggerKey.triggerKey("fuuid", "borgroup"));
        assertNull(couchdbStore.retrieveTrigger(TriggerKey.triggerKey("fuuid", "borgroup")));
    }

    @Test
    public void shouldDeleteExistingTriggers() throws JobPersistenceException {
        SimpleTriggerImpl trigger1 = (SimpleTriggerImpl) newTrigger()
            .withIdentity("fuuid1", "borgroup")
            .forJob(JobKey.jobKey("fooid", "bargroup"))
            .startAt(new Date(2010, 10, 20))
            .withSchedule(simpleSchedule()
                .withIntervalInMinutes(2)
                .repeatForever())
            .build();
        SimpleTriggerImpl trigger2 = (SimpleTriggerImpl) newTrigger()
            .withIdentity("fuuid2", "borgroup")
            .forJob(JobKey.jobKey("fooid", "bargroup"))
            .startAt(new Date(2010, 10, 20))
            .withSchedule(simpleSchedule()
                .withIntervalInMinutes(2)
                .repeatForever())
            .build();
        couchdbStore.storeTrigger(trigger1, false);
        couchdbStore.storeTrigger(trigger2, false);

        couchdbStore.removeTriggers(asList(
            TriggerKey.triggerKey("fuuid1", "borgroup"),
            TriggerKey.triggerKey("fuuid2", "borgroup")
        ));

        assertNull(couchdbStore.retrieveTrigger(TriggerKey.triggerKey("fuuid1", "borgroup")));
        assertNull(couchdbStore.retrieveTrigger(TriggerKey.triggerKey("fuuid2", "borgroup")));
    }

    @Test
    public void shouldCheckWhetherTriggerExists() throws JobPersistenceException {
        SimpleTriggerImpl trigger = (SimpleTriggerImpl) newTrigger()
            .withIdentity("fuuid", "borgroup")
            .forJob(JobKey.jobKey("fooid", "bargroup"))
            .startAt(new Date(2010, 10, 20))
            .withSchedule(simpleSchedule()
                .withIntervalInMinutes(2)
                .repeatForever())
            .build();

        assertFalse(couchdbStore.checkExists(TriggerKey.triggerKey("fuuid", "borgroup")));
        couchdbStore.storeTrigger(trigger, false);
        assertTrue(couchdbStore.checkExists(TriggerKey.triggerKey("fuuid", "borgroup")));
    }

    @Test
    public void shouldReplaceExistingTrigger() throws JobPersistenceException {
        SimpleTriggerImpl trigger = (SimpleTriggerImpl) newTrigger()
            .withIdentity("fuuid", "borgroup")
            .forJob(JobKey.jobKey("fooid", "bargroup"))
            .startAt(new Date(2010, 10, 20))
            .withSchedule(simpleSchedule()
                .withIntervalInMinutes(2)
                .repeatForever())
            .build();
        couchdbStore.storeTrigger(trigger, false);

        SimpleTriggerImpl newTrigger = (SimpleTriggerImpl) newTrigger()
            .withIdentity("fuuid", "borgroup")
            .forJob(JobKey.jobKey("fooid", "bargroup"))
            .startAt(new Date(2012, 10, 20))
            .withSchedule(simpleSchedule()
                .withIntervalInMinutes(2)
                .repeatForever())
            .build();

        assertTrue(couchdbStore.replaceTrigger(TriggerKey.triggerKey("fuuid", "borgroup"), newTrigger));
        assertEquals(new Date(2012, 10, 20), couchdbStore.retrieveTrigger(TriggerKey.triggerKey("fuuid", "borgroup")).getStartTime());
    }

    @Test(expected = JobPersistenceException.class)
    public void shouldNotReplaceExistingTriggerIdJobIsDifferent() throws JobPersistenceException {
        SimpleTriggerImpl trigger = (SimpleTriggerImpl) newTrigger()
            .withIdentity("fuuid", "borgroup")
            .forJob(JobKey.jobKey("fooid1", "bargroup"))
            .startAt(new Date(2010, 10, 20))
            .withSchedule(simpleSchedule()
                .withIntervalInMinutes(2)
                .repeatForever())
            .build();
        couchdbStore.storeTrigger(trigger, false);

        SimpleTriggerImpl newTrigger = (SimpleTriggerImpl) newTrigger()
            .withIdentity("fuuid", "borgroup")
            .forJob(JobKey.jobKey("fooid2", "bargroup"))
            .startAt(new Date(2012, 10, 20))
            .withSchedule(simpleSchedule()
                .withIntervalInMinutes(2)
                .repeatForever())
            .build();
        couchdbStore.replaceTrigger(TriggerKey.triggerKey("fuuid", "borgroup"), newTrigger);
    }

    @Test
    public void shouldNotReplaceNonExistingTrigger() throws JobPersistenceException {
        SimpleTriggerImpl trigger = (SimpleTriggerImpl) newTrigger()
            .withIdentity("fuuid", "borgroup")
            .forJob(JobKey.jobKey("fooid", "bargroup"))
            .startAt(new Date(2010, 10, 20))
            .withSchedule(simpleSchedule()
                .withIntervalInMinutes(2)
                .repeatForever())
            .build();

        assertFalse(couchdbStore.replaceTrigger(TriggerKey.triggerKey("fuuid", "borgroup"), trigger));
    }

    @Test
    public void shouldCountNumberOfTriggers() throws JobPersistenceException {
        SimpleTriggerImpl trigger = (SimpleTriggerImpl) newTrigger()
            .withIdentity("fuuid1", "borgroup")
            .forJob(JobKey.jobKey("fooid", "bargroup"))
            .startAt(new Date(2010, 10, 20))
            .withSchedule(simpleSchedule()
                .withIntervalInMinutes(2)
                .repeatForever())
            .build();
        couchdbStore.storeTrigger(trigger, false);

        SimpleTriggerImpl newTrigger = (SimpleTriggerImpl) newTrigger()
            .withIdentity("fuuid2", "borgroup")
            .forJob(JobKey.jobKey("fooid", "bargroup"))
            .startAt(new Date(2012, 10, 20))
            .withSchedule(simpleSchedule()
                .withIntervalInMinutes(2)
                .repeatForever())
            .build();
        couchdbStore.storeTrigger(newTrigger, false);

        assertEquals(2, couchdbStore.getNumberOfTriggers());
    }

    @Test
    public void shouldReturnMatchingTriggerKeys() throws JobPersistenceException {
        SimpleTriggerImpl trigger = (SimpleTriggerImpl) newTrigger()
            .withIdentity("fuuid1", "borgroup")
            .forJob(JobKey.jobKey("fooid", "bargroup"))
            .startAt(new Date(2010, 10, 20))
            .withSchedule(simpleSchedule()
                .withIntervalInMinutes(2)
                .repeatForever())
            .build();
        couchdbStore.storeTrigger(trigger, false);

        SimpleTriggerImpl newTrigger = (SimpleTriggerImpl) newTrigger()
            .withIdentity("fuuid2", "borgroup")
            .forJob(JobKey.jobKey("fooid", "bargroup"))
            .startAt(new Date(2012, 10, 20))
            .withSchedule(simpleSchedule()
                .withIntervalInMinutes(2)
                .repeatForever())
            .build();
        couchdbStore.storeTrigger(newTrigger, false);

        assertEquals(
            new HashSet<>(asList(TriggerKey.triggerKey("fuuid1", "borgroup"), TriggerKey.triggerKey("fuuid2", "borgroup"))),
            couchdbStore.getTriggerKeys(GroupMatcher.<TriggerKey>groupEquals("borgroup")));
    }

    @Test
    public void shouldReturnAllTriggerGroupNames() throws JobPersistenceException {
        SimpleTriggerImpl trigger = (SimpleTriggerImpl) newTrigger()
            .withIdentity("fuuid1", "borgroup1")
            .forJob(JobKey.jobKey("fooid", "bargroup"))
            .startAt(new Date(2010, 10, 20))
            .withSchedule(simpleSchedule()
                .withIntervalInMinutes(2)
                .repeatForever())
            .build();
        couchdbStore.storeTrigger(trigger, false);

        SimpleTriggerImpl newTrigger = (SimpleTriggerImpl) newTrigger()
            .withIdentity("fuuid2", "borgroup2")
            .forJob(JobKey.jobKey("fooid", "bargroup"))
            .startAt(new Date(2012, 10, 20))
            .withSchedule(simpleSchedule()
                .withIntervalInMinutes(2)
                .repeatForever())
            .build();
        couchdbStore.storeTrigger(newTrigger, false);

        assertEquals(asList("borgroup1" ,"borgroup2"), couchdbStore.getTriggerGroupNames());
    }

    @Test
    public void shouldReturnTriggerState() throws JobPersistenceException {
        SimpleTriggerImpl trigger = (SimpleTriggerImpl) newTrigger()
            .withIdentity("fuuid1", "borgroup1")
            .forJob(JobKey.jobKey("fooid", "bargroup"))
            .startAt(new Date(2010, 10, 20))
            .withSchedule(simpleSchedule()
                .withIntervalInMinutes(2)
                .repeatForever())
            .build();
        couchdbStore.storeTrigger(trigger, false);

        assertEquals(Trigger.TriggerState.NORMAL, couchdbStore.getTriggerState(TriggerKey.triggerKey("fuuid1", "borgroup1")));
    }

    @Test
    public void shouldAcquireTriggersToFire() throws JobPersistenceException {
        SimpleTriggerImpl trigger1 = (SimpleTriggerImpl) newTrigger()
            .withIdentity("fuuid1", "borgroup1")
            .forJob(JobKey.jobKey("fooid", "bargroup"))
            .startAt(newDate(2010, 10, 20).toDate())
            .withSchedule(simpleSchedule()
                .withIntervalInMinutes(2)
                .repeatForever())
            .build();
        trigger1.computeFirstFireTime(null);
        couchdbStore.storeTrigger(trigger1, false);

        SimpleTriggerImpl trigger2 = (SimpleTriggerImpl) newTrigger()
            .withIdentity("fuuid2", "borgroup2")
            .forJob(JobKey.jobKey("fooid", "bargroup"))
            .startAt(newDate(2010, 10, 22).toDate())
            .withSchedule(simpleSchedule()
                .withIntervalInMinutes(2)
                .repeatForever())
            .build();
        trigger2.computeFirstFireTime(null);
        couchdbStore.storeTrigger(trigger2, false);

        List<OperableTrigger> triggers = couchdbStore.acquireNextTriggers(newDateTime(2010, 10, 21).getMillis(), 10, 0);
        assertEquals(1, triggers.size());
        assertEquals(TriggerKey.triggerKey("fuuid1", "borgroup1"), triggers.get(0).getKey());
    }

    @Test
    public void shouldFireTriggers() throws JobPersistenceException {
        SimpleTriggerImpl trigger = (SimpleTriggerImpl) newTrigger()
            .withIdentity("fuuid1", "borgroup1")
            .forJob(JobKey.jobKey("fooid", "bargroup"))
            .startAt(newDate(2010, 10, 20).toDate())
            .withSchedule(simpleSchedule()
                .withIntervalInMinutes(2)
                .repeatForever())
            .build();
        trigger.computeFirstFireTime(null);
        couchdbStore.storeTrigger(trigger, false);

        List<TriggerFiredResult> firedResults = couchdbStore.triggersFired(Arrays.<OperableTrigger>asList(trigger));

        assertEquals(1, firedResults.size());
        assertEquals(TriggerKey.triggerKey("fuuid1", "borgroup1"), firedResults.get(0).getTriggerFiredBundle().getTrigger().getKey());
    }

    @Test
    public void shouldDeleteTriggerAfterFire() throws JobPersistenceException {
        JobDetail job = newJob(MotechScheduledJob.class)
            .withIdentity("fooid", "bargroup")
            .usingJobData("foo", "bar")
            .usingJobData("fuu", "baz")
            .build();
        couchdbStore.storeJob(job, false);

        SimpleTriggerImpl trigger = (SimpleTriggerImpl) newTrigger()
            .withIdentity("fuuid1", "borgroup1")
            .forJob(JobKey.jobKey("fooid", "bargroup"))
            .startAt(newDate(2010, 10, 20).toDate())
            .withSchedule(simpleSchedule()
                .withIntervalInMinutes(2)
                .withRepeatCount(0))
            .build();
        trigger.computeFirstFireTime(null);
        couchdbStore.storeTrigger(trigger, false);

        trigger.triggered(null);

        couchdbStore.triggeredJobComplete(trigger, job, null);

        assertNull(couchdbStore.retrieveTrigger(TriggerKey.triggerKey("fuuid1", "borgroup1")));
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

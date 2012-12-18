package org.motechproject.scheduler.quartz;

import org.ektorp.CouchDbConnector;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.ektorp.spring.HttpClientFactoryBean;
import org.ektorp.support.View;
import org.quartz.Calendar;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.JobPersistenceException;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerConfigException;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.spi.ClassLoadHelper;
import org.quartz.spi.JobStore;
import org.quartz.spi.OperableTrigger;
import org.quartz.spi.SchedulerSignaler;
import org.quartz.spi.TriggerFiredBundle;
import org.quartz.spi.TriggerFiredResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@Repository
public class CouchdbStore implements JobStore {

    protected String instanceId;
    protected String instanceName;
    private int theadPoolSize;

    @Autowired
    private CouchdbJobStore jobStore;
    @Autowired
    private CouchdbTriggerStore triggerStore;

    public CouchdbStore() {
    }

    public void setProperties(String propertiesFile) throws Exception {
        Properties properties = new Properties();
        properties.load(ClassLoader.class.getResourceAsStream(propertiesFile));

        HttpClientFactoryBean httpClientFactoryBean = new HttpClientFactoryBean();
        httpClientFactoryBean.setProperties(properties);
        httpClientFactoryBean.setCaching(false);
        httpClientFactoryBean.afterPropertiesSet();

        CouchDbConnector connector = new StdCouchDbConnector("scheduler", new StdCouchDbInstance(httpClientFactoryBean.getObject()));

        this.jobStore = new CouchdbJobStore(connector);
        this.triggerStore = new CouchdbTriggerStore(connector);
    }

    @Override
    public void initialize(ClassLoadHelper loadHelper, SchedulerSignaler signaler) throws SchedulerConfigException {
    }

    @Override
    public void schedulerStarted() throws SchedulerException {
    }

    @Override
    public void schedulerPaused() {
    }

    @Override
    public void schedulerResumed() {
    }

    @Override
    public void shutdown() {
    }

    @Override
    public boolean supportsPersistence() {
        return true;
    }

    @Override
    public long getEstimatedTimeToReleaseAndAcquireTrigger() {
        return 0;
    }

    @Override
    public boolean isClustered() {
        return false;
    }

    @Override
    public void storeJobAndTrigger(JobDetail newJob, OperableTrigger newTrigger) throws ObjectAlreadyExistsException, JobPersistenceException {
        jobStore.storeJob(newJob, false);
        triggerStore.storeTrigger(newTrigger, false);
    }

    @Override
    public void storeJob(JobDetail newJob, boolean replaceExisting) throws ObjectAlreadyExistsException, JobPersistenceException {
        jobStore.storeJob(newJob, replaceExisting);
    }

    @Override
    public void storeJobsAndTriggers(Map<JobDetail, List<Trigger>> triggersAndJobs, boolean replace) throws ObjectAlreadyExistsException, JobPersistenceException {
        if (!replace) {
            for (Map.Entry<JobDetail, List<Trigger>> e: triggersAndJobs.entrySet()) {
                if (checkExists(e.getKey().getKey())) {
                    throw new ObjectAlreadyExistsException(e.getKey());
                }
                for (Trigger trigger: e.getValue()) {
                    if (checkExists(trigger.getKey())) {
                        throw new ObjectAlreadyExistsException(trigger);
                    }
                }
            }
        }
        for (Map.Entry<JobDetail, List<Trigger>> e: triggersAndJobs.entrySet()) {
            storeJob(e.getKey(), true);
            for (Trigger trigger: e.getValue()) {
                storeTrigger((OperableTrigger) trigger, true);
            }
        }
    }

    // TODO: delete job group if empty
    @Override
    public boolean removeJob(JobKey jobKey) throws JobPersistenceException {
        for (OperableTrigger trigger : getTriggersForJob(jobKey)) {
            this.removeTrigger(trigger.getKey());
        }
        return jobStore.removeJob(jobKey);
    }

    @Override
    public boolean removeJobs(List<JobKey> jobKeys) throws JobPersistenceException {
        return jobStore.removeJobs(jobKeys);
    }

    @Override
    @View(name = "by_jobkey", map = "function(doc) { if (doc.type === 'CouchdbJobDetail') emit([doc.name, doc.group], doc._id); }")
    public JobDetail retrieveJob(JobKey jobKey) throws JobPersistenceException {
        return jobStore.retrieveJob(jobKey);
    }

    @Override
    public void storeTrigger(OperableTrigger newTrigger, boolean replaceExisting) throws ObjectAlreadyExistsException, JobPersistenceException {
        triggerStore.storeTrigger(newTrigger, replaceExisting);
    }

    @Override
    public boolean removeTrigger(TriggerKey triggerKey) throws JobPersistenceException {
        return triggerStore.removeTrigger(triggerKey);
    }

    @Override
    public boolean removeTriggers(List<TriggerKey> triggerKeys) throws JobPersistenceException {
        return triggerStore.removeTriggers(triggerKeys);
    }

    @Override
    public boolean replaceTrigger(TriggerKey triggerKey, OperableTrigger newTrigger) throws JobPersistenceException {
        return triggerStore.replaceTrigger(triggerKey, newTrigger);
    }

    @Override
    @View(name = "by_triggerkey", map = "function(doc) { if (doc.type === 'CouchdbTrigger') emit([doc.name, doc.group], doc._id); }")
    public OperableTrigger retrieveTrigger(TriggerKey triggerKey) throws JobPersistenceException {
        return triggerStore.retrieveTrigger(triggerKey);
    }

    @Override
    public boolean checkExists(JobKey jobKey) throws JobPersistenceException {
        return jobStore.checkExists(jobKey);
    }

    @Override
    public boolean checkExists(TriggerKey triggerKey) throws JobPersistenceException {
        return triggerStore.checkExists(triggerKey);
    }

    @Override
    public void clearAllSchedulingData() throws JobPersistenceException {
    }

    @Override
    public void storeCalendar(String name, Calendar calendar, boolean replaceExisting, boolean updateTriggers) throws ObjectAlreadyExistsException, JobPersistenceException {
    }

    @Override
    public boolean removeCalendar(String calName) throws JobPersistenceException {
        return false;
    }

    @Override
    public Calendar retrieveCalendar(String calName) throws JobPersistenceException {
        return null;
    }

    @Override
    public int getNumberOfJobs() throws JobPersistenceException {
        return jobStore.getNumberOfJobs();
    }

    @Override
    public int getNumberOfTriggers() throws JobPersistenceException {
        return triggerStore.getNumberOfTriggers();
    }

    @Override
    public int getNumberOfCalendars() throws JobPersistenceException {
        return 0;
    }

    @Override
    public Set<JobKey> getJobKeys(GroupMatcher<JobKey> matcher) throws JobPersistenceException {
        return jobStore.getJobKeys(matcher);
    }

    @Override
    public Set<TriggerKey> getTriggerKeys(GroupMatcher<TriggerKey> matcher) throws JobPersistenceException {
        return triggerStore.getTriggerKeys(matcher);
    }

    @Override
    public List<String> getJobGroupNames() throws JobPersistenceException {
        return jobStore.getJobGroupNames();
    }

    @Override
    public List<String> getTriggerGroupNames() throws JobPersistenceException {
        return triggerStore.getTriggerGroupNames();
    }

    @Override
    public List<String> getCalendarNames() throws JobPersistenceException {
        return new ArrayList<>();
    }

    @Override
    public List<OperableTrigger> getTriggersForJob(JobKey jobKey) throws JobPersistenceException {
        return triggerStore.findByJob(jobKey);
    }

    @Override
    public Trigger.TriggerState getTriggerState(TriggerKey triggerKey) throws JobPersistenceException {
        return triggerStore.getTriggerState(triggerKey);
    }

    @Override
    public void pauseTrigger(TriggerKey triggerKey) throws JobPersistenceException {
    }

    @Override
    public Collection<String> pauseTriggers(GroupMatcher<TriggerKey> matcher) throws JobPersistenceException {
        return null;
    }

    @Override
    public void pauseJob(JobKey jobKey) throws JobPersistenceException {
    }

    @Override
    public Collection<String> pauseJobs(GroupMatcher<JobKey> groupMatcher) throws JobPersistenceException {
        return null;
    }

    @Override
    public void resumeTrigger(TriggerKey triggerKey) throws JobPersistenceException {
    }

    @Override
    public Collection<String> resumeTriggers(GroupMatcher<TriggerKey> matcher) throws JobPersistenceException {
        return null;
    }

    @Override
    public Set<String> getPausedTriggerGroups() throws JobPersistenceException {
        return null;
    }

    @Override
    public void resumeJob(JobKey jobKey) throws JobPersistenceException {
    }

    @Override
    public Collection<String> resumeJobs(GroupMatcher<JobKey> matcher) throws JobPersistenceException {
        return null;
    }

    @Override
    public void pauseAll() throws JobPersistenceException {
    }

    @Override
    public void resumeAll() throws JobPersistenceException {
    }

    @Override
    public List<OperableTrigger> acquireNextTriggers(long noLaterThan, int maxCount, long timeWindow) throws JobPersistenceException {
        return triggerStore.acquireNextTriggers(noLaterThan, maxCount, timeWindow);
    }

    @Override
    public void releaseAcquiredTrigger(OperableTrigger trigger) throws JobPersistenceException {
    }

    @Override
    public List<TriggerFiredResult> triggersFired(List<OperableTrigger> triggers) throws JobPersistenceException {
        List<TriggerFiredResult> firedResults = new ArrayList<>();
        for (OperableTrigger trigger : triggers) {

            Date prevFireTime = trigger.getPreviousFireTime();
            trigger.triggered(null);

            storeTrigger(trigger, true);

            JobDetail job = jobStore.retrieveJob(trigger.getJobKey());
            firedResults.add(new TriggerFiredResult(new TriggerFiredBundle(job, trigger, null, trigger.getKey().getGroup().equals(Scheduler.DEFAULT_RECOVERY_GROUP), new Date(), trigger.getPreviousFireTime(), prevFireTime, trigger.getNextFireTime())));
        }
        return firedResults;
    }

    @Override
    public void triggeredJobComplete(OperableTrigger trigger, JobDetail jobDetail, Trigger.CompletedExecutionInstruction triggerInstCode) throws JobPersistenceException {
        JobDetail job = jobStore.retrieveJob(trigger.getJobKey());
        triggerStore.triggeredJobComplete(trigger, job, null);
    }

    @Override
    public void setInstanceId(String schedInstId) {
        this.instanceId = schedInstId;
    }

    @Override
    public void setInstanceName(String schedName) {
        this.instanceName = schedName;
    }

    @Override
    public void setThreadPoolSize(int poolSize) {
        theadPoolSize = poolSize;
    }
}

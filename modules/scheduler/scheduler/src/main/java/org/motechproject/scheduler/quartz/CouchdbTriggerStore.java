package org.motechproject.scheduler.quartz;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.JobPersistenceException;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.AbstractTrigger;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.quartz.spi.OperableTrigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

@Repository
public class CouchdbTriggerStore extends CouchDbRepositorySupport {

    @Autowired
    protected CouchdbTriggerStore(@Qualifier("schedulerConnector") CouchDbConnector db) {
        super(CouchdbTrigger.class, db);
        initStandardDesignDocument();
    }

    public void storeTrigger(OperableTrigger newTrigger, boolean replaceExisting) throws ObjectAlreadyExistsException, JobPersistenceException {
        CouchdbTrigger existingTrigger = (CouchdbTrigger) retrieveTrigger(newTrigger.getKey());
        if (existingTrigger == null) {
            if (newTrigger instanceof SimpleTriggerImpl) {
                db.create(new CouchdbSimpleTrigger((SimpleTriggerImpl) newTrigger));
            }
            return;
        }
        if (replaceExisting) {
            if (newTrigger instanceof SimpleTriggerImpl) {
                db.update(existingTrigger.merge((AbstractTrigger) newTrigger));
            }
        } else {
            throw new ObjectAlreadyExistsException("trigger already exists");
        }
    }

    public boolean removeTrigger(TriggerKey triggerKey) throws JobPersistenceException {
        OperableTrigger trigger = retrieveTrigger(triggerKey);
        if (trigger == null) {
            return false;
        }
        db.delete(trigger);
        return true;
    }

    public boolean removeTriggers(List<TriggerKey> triggerKeys) throws JobPersistenceException {
        boolean allFound = true;
        for (TriggerKey key : triggerKeys) {
            allFound = removeTrigger(key) && allFound;
        }
        return allFound;
    }

    public boolean replaceTrigger(TriggerKey triggerKey, OperableTrigger newTrigger) throws JobPersistenceException {
        CouchdbTrigger existingTrigger = (CouchdbTrigger) retrieveTrigger(triggerKey);
        if (existingTrigger == null) {
            return false;
        }
        if (!(existingTrigger.getJobName().equals(newTrigger.getJobKey().getName()) && existingTrigger.getJobGroup().equals(newTrigger.getJobKey().getGroup()))) {
            throw new JobPersistenceException("New trigger is not related to the same job as the old trigger.");
        }
        if (newTrigger instanceof SimpleTriggerImpl) {
            db.update(existingTrigger.merge((AbstractTrigger) newTrigger));
        }
        return true;
    }

    @View(name = "by_triggerkey", map = "function(doc) { if (doc.type === 'CouchdbTrigger') emit([doc.trigger_name, doc.trigger_group], doc._id); }")
    public OperableTrigger retrieveTrigger(TriggerKey triggerKey) throws JobPersistenceException {
        List<CouchdbTrigger> triggers = db.queryView(createQuery("by_triggerkey").key(ComplexKey.of(triggerKey.getName(), triggerKey.getGroup())).includeDocs(true), type);
        return triggers != null && triggers.size() > 0? (OperableTrigger) triggers.get(0) : null;
    }

    public boolean checkExists(TriggerKey triggerKey) throws JobPersistenceException {
        return retrieveTrigger(triggerKey) != null;
    }

    @View(name = "by_jobkey", map = "function(doc) { if (doc.type === 'CouchdbTrigger') emit([doc.job_name, doc.job_group], doc._id); }")
    public List<OperableTrigger> findByJob(JobKey jobKey) {
        return db.queryView(createQuery("by_jobkey").key(ComplexKey.of(jobKey.getName(), jobKey.getGroup())).includeDocs(true), type);
    }

    @View(name = "all_triggers", map = "function(doc) { if (doc.type === 'CouchdbTrigger') emit(doc._id, doc._id); }")
    public List<CouchdbTrigger> getAll() {
        return db.queryView(createQuery("all_triggers").includeDocs(true), type);
    }

    public int getNumberOfTriggers() {
        return getAll().size();
    }

    public Set<TriggerKey> getTriggerKeys(GroupMatcher<TriggerKey> matcher) {
        Set<TriggerKey> matchedKeys = new HashSet<>();
        for (CouchdbTrigger couchdbTrigger : getAll()) {
            AbstractTrigger trigger = (AbstractTrigger) couchdbTrigger;
            if (matcher.isMatch(trigger.getKey())) {
                matchedKeys.add(trigger.getKey());
            }
        }
        return matchedKeys;
    }

    @View(name = "by_triggerGroupName", map = "function(doc) { if (doc.type === 'CouchdbTrigger') emit(doc.group, doc._id); }")
    public List<String> getTriggerGroupNames() {
        return new ArrayList<>(new HashSet<>(extract(db.queryView(createQuery("by_triggerGroupName").includeDocs(true), type), on(CouchdbTrigger.class).getGroup())));
    }

    @View(name = "by_triggeState", map = "function(doc) { if (doc.type === 'CouchdbTrigger') emit(doc.state, doc._id); }")
    public Trigger.TriggerState getTriggerState(TriggerKey triggerKey) {
        List<CouchdbTrigger> triggers = db.queryView(createQuery("by_triggerGroupName").includeDocs(true), type);
        if (triggers == null || triggers.size() == 0) {
            return null;
        }
        return triggers.get(0).getState();
    }

    @View(name = "by_nextFireTime", map = "function(doc) { if (doc.type === 'CouchdbTrigger') emit(doc.next_fire_time, doc._id); }")
    public List<OperableTrigger> acquireNextTriggers(long noLaterThan, int maxCount, long timeWindow) throws JobPersistenceException {
        List<CouchdbTrigger> couchdbTriggers = db.queryView(createQuery("by_nextFireTime").startKey(new Date(0)).endKey(new Date(noLaterThan + timeWindow)).limit(maxCount).includeDocs(true), CouchdbTrigger.class);
        List<OperableTrigger> operableTriggers = new ArrayList<>();
        for (CouchdbTrigger couchdbTrigger : couchdbTriggers) {
            operableTriggers.add((OperableTrigger) couchdbTrigger);
        }
        return operableTriggers;
    }

    public List<OperableTrigger> triggersFired(List<OperableTrigger> triggers) throws JobPersistenceException {
        List<OperableTrigger> firedTriggers = new ArrayList<>();
        for (OperableTrigger trigger : triggers) {
            trigger.triggered(null);
            storeTrigger(trigger, true);
            firedTriggers.add(trigger);
        }
        return firedTriggers;
    }

    public void triggeredJobComplete(OperableTrigger trigger, JobDetail job, Trigger.CompletedExecutionInstruction triggerInstCode) throws JobPersistenceException {
        if (trigger.getNextFireTime() == null) {
            removeTrigger(trigger.getKey());
        }
    }
}

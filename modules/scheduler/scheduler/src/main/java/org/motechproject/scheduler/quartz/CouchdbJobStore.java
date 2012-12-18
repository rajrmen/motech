package org.motechproject.scheduler.quartz;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.JobPersistenceException;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

@Repository
public class CouchdbJobStore extends CouchDbRepositorySupport {

    private int numberOfJobs;

    @Autowired
    protected CouchdbJobStore(@Qualifier("schedulerConnector") CouchDbConnector db) {
        super(CouchdbJobDetail.class, db);
        initStandardDesignDocument();
    }

    public void storeJob(JobDetail newJob, boolean replaceExisting) throws ObjectAlreadyExistsException, JobPersistenceException {
        CouchdbJobDetail existingJob = (CouchdbJobDetail) retrieveJob(newJob.getKey());
        if (existingJob == null) {
            db.create(new CouchdbJobDetail(newJob));
            return;
        }
        if (replaceExisting) {
            db.update(existingJob.merge(newJob));
        } else {
            throw new ObjectAlreadyExistsException("job already exists");
        }
    }

    public boolean removeJob(JobKey jobKey) throws JobPersistenceException {
        JobDetail job = retrieveJob(jobKey);
        if (job == null) {
            return false;
        }
        db.delete(job);
        return true;
    }

    public boolean removeJobs(List<JobKey> jobKeys) throws JobPersistenceException {
        boolean allFound = true;
        for (JobKey key : jobKeys) {
            allFound = removeJob(key) && allFound;
        }
        return allFound;
    }

    public boolean checkExists(JobKey jobKey) throws JobPersistenceException {
        return retrieveJob(jobKey) != null;
    }

    @View(name = "by_jobKey", map = "function(doc) { if (doc.type === 'CouchdbJobDetail') emit([doc.name, doc.group], doc._id); }")
    public JobDetail retrieveJob(JobKey jobKey) throws JobPersistenceException {
        List<JobDetail> jobs = db.queryView(createQuery("by_jobKey").key(ComplexKey.of(jobKey.getName(), jobKey.getGroup())).includeDocs(true), type);
        return jobs != null && jobs.size() > 0? jobs.get(0) : null;
    }

    @View(name = "all_jobs", map = "function(doc) { if (doc.type === 'CouchdbJobDetail') emit(doc._id, doc._id); }")
    public List<CouchdbJobDetail> getAll() {
        return db.queryView(createQuery("all_jobs").includeDocs(true), type);
    }

    public int getNumberOfJobs() {
        return getAll().size();
    }

    public Set<JobKey> getJobKeys(GroupMatcher<JobKey> matcher) {
        Set<JobKey> matchedKeys = new HashSet<>();
        for (CouchdbJobDetail jobDetail : getAll()) {
            if (matcher.isMatch(jobDetail.getKey())) {
                matchedKeys.add(jobDetail.getKey());
            }
        }
        return matchedKeys;
    }

    @View(name = "by_jobGroupName", map = "function(doc) { if (doc.type === 'CouchdbJobDetail') emit(doc.group, doc._id); }")
    public List<String> getJobGroupNames() {
        return new ArrayList<>(new HashSet<>(extract(db.queryView(createQuery("by_jobGroupName").includeDocs(true), type), on(CouchdbJobDetail.class).getGroup())));
    }
}

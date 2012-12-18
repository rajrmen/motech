package org.motechproject.scheduler.quartz;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.impl.JobDetailImpl;

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.NONE,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE)
@TypeDiscriminator("CouchdbJobDetail")
public class CouchdbJobDetail extends JobDetailImpl {

    private String id;
    private String revision;
    private String type;

    private CouchdbJobDetail() {
        super();
        type = "CouchdbJobDetail";
    }

    public CouchdbJobDetail(JobDetail newJob) {
        this();
        if (newJob instanceof JobDetailImpl) {
            setName(((JobDetailImpl) newJob).getName());
            setGroup(((JobDetailImpl) newJob).getGroup());
        } else if (newJob instanceof CouchdbJobDetail) {
            setName((((CouchdbJobDetail) newJob).getName()));
            setGroup(((CouchdbJobDetail) newJob).getGroup());
        }
        setDescription(newJob.getDescription());
        setJobClass(newJob.getJobClass());
        setDurability(newJob.isDurable());
        setRequestsRecovery(newJob.requestsRecovery());
        setJobDataMap((JobDataMap) newJob.getJobDataMap().clone());
    }

    @JsonProperty("_id")
    public String getId() {
        return id;
    }

    @JsonProperty("_id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("_rev")
    public String getRevision() {
        return revision;
    }

    @JsonProperty("_rev")
    public void setRevision(String revision) {
        this.revision = revision;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @Override
    @JsonProperty("name")
    public String getName() {
        return super.getName();
    }

    @Override
    @JsonProperty("name")
    public void setName(String name) {
        super.setName(name);
    }

    @Override
    @JsonProperty("group")
    public String getGroup() {
        return super.getGroup();
    }

    @Override
    @JsonProperty("group")
    public void setGroup(String group) {
        super.setGroup(group);
    }

    @Override
    @JsonProperty("description")
    public String getDescription() {
        return super.getDescription();
    }

    @Override
    @JsonProperty("description")
    public void setDescription(String description) {
        super.setDescription(description);
    }

    @Override
    @JsonProperty("jobClass")
    public Class<? extends Job> getJobClass() {
        return super.getJobClass();
    }

    @Override
    @JsonProperty("jobClass")
    public void setJobClass(Class<? extends Job> jobClass) {
        super.setJobClass(jobClass);
    }

    @Override
    @JsonProperty("jobDataMap")
    public JobDataMap getJobDataMap() {
        return super.getJobDataMap();
    }

    @Override
    @JsonProperty("jobDataMap")
    public void setJobDataMap(JobDataMap jobDataMap) {
        super.setJobDataMap(jobDataMap);
    }

    @Override
    @JsonProperty("durability")
    public boolean isDurable() {
        return super.isDurable();
    }

    @Override
    @JsonProperty("durability")
    public void setDurability(boolean durability) {
        super.setDurability(durability);
    }

    @Override
    @JsonProperty("shouldRecover")
    public boolean requestsRecovery() {
        return super.requestsRecovery();
    }

    @Override
    @JsonProperty("shouldRecover")
    public void setRequestsRecovery(boolean shouldRecover) {
        super.setRequestsRecovery(shouldRecover);
    }

    public CouchdbJobDetail merge(JobDetail other) {
        this.setDescription(other.getDescription());
        this.setJobClass(other.getJobClass());
        this.setJobDataMap((JobDataMap) other.getJobDataMap().clone());
        this.setRequestsRecovery(other.requestsRecovery());
        this.setDurability(other.isDurable());
        return this;
    }
}

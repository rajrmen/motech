package org.motechproject.scheduler.quartz;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.quartz.JobDataMap;
import org.quartz.impl.triggers.AbstractTrigger;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import java.util.Date;

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.NONE,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE)
@TypeDiscriminator("CouchdbTrigger")
public class CouchdbSimpleTrigger extends SimpleTriggerImpl implements CouchdbTrigger<CouchdbSimpleTrigger> {

    private String id;
    private String revision;
    private String type;

    private TriggerState state;

    private CouchdbSimpleTrigger() {
        setType("CouchdbTrigger");
    }

    public CouchdbSimpleTrigger(SimpleTriggerImpl trigger) {
        this();
        setName(trigger.getName());
        setGroup(trigger.getGroup());
        setJobName(trigger.getJobName());
        setJobGroup(trigger.getJobGroup());
        setDescription(trigger.getDescription());
        setNextFireTime(trigger.getNextFireTime());
        setPreviousFireTime(trigger.getPreviousFireTime());
        setPriority(trigger.getPriority());
        setState(TriggerState.NORMAL);
        setStartTime(trigger.getStartTime());
        setEndTime(trigger.getEndTime());
        setCalendarName(trigger.getCalendarName());
        setMisfireInstruction(trigger.getMisfireInstruction());
        setJobDataMap((JobDataMap) trigger.getJobDataMap().clone());
        setRepeatCount(trigger.getRepeatCount());
        setRepeatInterval(trigger.getRepeatInterval());
        setTimesTriggered(trigger.getTimesTriggered());
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
    @JsonProperty("trigger_name")
    public String getName() {
        return super.getName();
    }

    @Override
    @JsonProperty("trigger_name")
    public void setName(String name) {
        super.setName(name);
    }

    @Override
    @JsonProperty("trigger_group")
    public String getGroup() {
        return super.getGroup();
    }

    @Override
    @JsonProperty("trigger_group")
    public void setGroup(String group) {
        super.setGroup(group);
    }

    @Override
    @JsonProperty("job_name")
    public String getJobName() {
        return super.getJobName();
    }

    @Override
    @JsonProperty("job_name")
    public void setJobName(String jobName) {
        super.setJobName(jobName);
    }

    @Override
    @JsonProperty("job_group")
    public String getJobGroup() {
        return super.getJobGroup();
    }

    @Override
    @JsonProperty("job_group")
    public void setJobGroup(String jobGroup) {
        super.setJobGroup(jobGroup);
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
    @JsonProperty("next_fire_time")
    public Date getNextFireTime() {
        return super.getNextFireTime();
    }

    @Override
    @JsonProperty("next_fire_time")
    public void setNextFireTime(Date nextFireTime) {
        super.setNextFireTime(nextFireTime);
    }

    @Override
    @JsonProperty("previous_fire_time")
    public Date getPreviousFireTime() {
        return super.getPreviousFireTime();
    }

    @Override
    @JsonProperty("previous_fire_time")
    public void setPreviousFireTime(Date previousFireTime) {
        super.setPreviousFireTime(previousFireTime);
    }

    @JsonProperty("priority")
    public Integer getPriorityValue() {
        return new Integer(super.getPriority());
    }

    @JsonProperty("priority")
    public void setPriorityValue(Integer priority) {
        super.setPriority(priority);
    }

    @JsonProperty("state")
    public TriggerState getState() {
        return state;
    }

    @JsonProperty("state")
    public void setState(TriggerState state) {
        this.state = state;
    }

    @Override
    @JsonProperty("start_time")
    public Date getStartTime() {
        return super.getStartTime();
    }

    @Override
    @JsonProperty("start_time")
    public void setStartTime(Date startTime) {
        super.setStartTime(startTime);
    }

    @Override
    @JsonProperty("end_time")
    public Date getEndTime() {
        return super.getEndTime();
    }

    @Override
    @JsonProperty("end_time")
    public void setEndTime(Date endTime) {
        super.setEndTime(endTime);
    }

    @Override
    @JsonProperty("calendar_name")
    public void setCalendarName(String calendarName) {
        super.setCalendarName(calendarName);
    }

    @Override
    @JsonProperty("calendar_name")
    public String getCalendarName() {
        return super.getCalendarName();
    }

    @JsonProperty("misfire_instruction")
    public void setMisfireInstructionValue(Integer misfireInstruction) {
        super.setMisfireInstruction(misfireInstruction);
    }

    @JsonProperty("misfire_instruction")
    public Integer getMisfireInstructionValue() {
        return new Integer(super.getMisfireInstruction());
    }

    @Override
    @JsonProperty("job_data")
    public JobDataMap getJobDataMap() {
        return super.getJobDataMap();
    }

    @Override
    @JsonProperty("job_data")
    public void setJobDataMap(JobDataMap jobDataMap) {
        super.setJobDataMap(jobDataMap);
    }

    @Override
    @JsonProperty("repeat_count")
    public int getRepeatCount() {
        return super.getRepeatCount();
    }

    @Override
    @JsonProperty("repeat_count")
    public void setRepeatCount(int repeatCount) {
        super.setRepeatCount(repeatCount);
    }

    @Override
    @JsonProperty("repeat_interval")
    public long getRepeatInterval() {
        return super.getRepeatInterval();
    }

    @Override
    @JsonProperty("repeat_interval")
    public void setRepeatInterval(long repeatInterval) {
        super.setRepeatInterval(repeatInterval);
    }

    @Override
    @JsonProperty("times_triggered")
    public int getTimesTriggered() {
        return super.getTimesTriggered();
    }

    @Override
    @JsonProperty("times_triggered")
    public void setTimesTriggered(int timesTriggered) {
        super.setTimesTriggered(timesTriggered);
    }

    @Override
    public CouchdbSimpleTrigger merge(AbstractTrigger newTrigger) {
        SimpleTriggerImpl simpleTrigger = (SimpleTriggerImpl) newTrigger;
        setName(simpleTrigger.getName());
        setGroup(simpleTrigger.getGroup());
        setJobName(simpleTrigger.getJobName());
        setJobGroup(simpleTrigger.getJobGroup());
        setDescription(simpleTrigger.getDescription());
        setNextFireTime(simpleTrigger.getNextFireTime());
        setPreviousFireTime(simpleTrigger.getPreviousFireTime());
        setPriority(simpleTrigger.getPriority());
        setState(TriggerState.NORMAL);
        setStartTime(simpleTrigger.getStartTime());
        setEndTime(simpleTrigger.getEndTime());
        setCalendarName(simpleTrigger.getCalendarName());
        setMisfireInstruction(simpleTrigger.getMisfireInstruction());
        setJobDataMap((JobDataMap) simpleTrigger.getJobDataMap().clone());
        setRepeatCount(simpleTrigger.getRepeatCount());
        setRepeatInterval(simpleTrigger.getRepeatInterval());
        setTimesTriggered(simpleTrigger.getTimesTriggered());
        return this;
    }
}

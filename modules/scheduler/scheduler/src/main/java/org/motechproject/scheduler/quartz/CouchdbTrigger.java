package org.motechproject.scheduler.quartz;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.quartz.Trigger;
import org.quartz.impl.triggers.AbstractTrigger;

@JsonTypeInfo(include = JsonTypeInfo.As.PROPERTY, use = JsonTypeInfo.Id.CLASS, property = "@type")
public interface CouchdbTrigger<T> {

    public String getName();
    public void setName(String name);

    public String getGroup();
    public void setGroup(String group);

    public String getJobName();
    public void setJobName(String jobName);

    public String getJobGroup();
    public void setJobGroup(String jobGroup);

    public String getDescription();
    public void setDescription(String description);

    public Trigger.TriggerState getState();
    public void setState(Trigger.TriggerState state);

    T merge(AbstractTrigger newTrigger);
}

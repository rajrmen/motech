package org.motechproject.scheduler.quartz;

import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.quartz.Trigger;
import org.quartz.impl.triggers.AbstractTrigger;

@JsonTypeInfo(include = JsonTypeInfo.As.PROPERTY, use = JsonTypeInfo.Id.CLASS, property = "@type")
public interface CouchdbTrigger<T> {

    String getName();
    void setName(String name);

    String getGroup();
    void setGroup(String group);

    String getJobName();
    void setJobName(String jobName);

    String getJobGroup();
    void setJobGroup(String jobGroup);

    String getDescription();
    void setDescription(String description);

    Trigger.TriggerState getState();
    void setState(Trigger.TriggerState state);

    T merge(AbstractTrigger newTrigger);
}

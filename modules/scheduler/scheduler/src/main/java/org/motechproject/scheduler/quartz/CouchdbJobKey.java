package org.motechproject.scheduler.quartz;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.quartz.JobKey;

public class CouchdbJobKey {

    @JsonProperty
    private String name;
    @JsonProperty
    private String group;

    private CouchdbJobKey() {
    }

    public CouchdbJobKey(JobKey key) {
        this.name = key.getName();
        this.group = key.getGroup();
    }

    @JsonIgnore
    public String getName() {
        return name;
    }

    @JsonIgnore
    public String getGroup() {
        return group;
    }
}

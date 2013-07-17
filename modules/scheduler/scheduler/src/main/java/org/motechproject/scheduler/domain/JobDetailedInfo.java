package org.motechproject.scheduler.domain;

import java.util.Map;

public class JobDetailedInfo {
    private JobBasicInfo jobBasicInfo;
    private String eventName;
    private String subject;
    private Map<String, Object> parameters;

    public JobDetailedInfo(JobBasicInfo jobBasicInfo) {
        this.jobBasicInfo = jobBasicInfo;
    }

    public JobDetailedInfo(JobBasicInfo jobBasicInfo, String eventName, String subject, Map<String, Object> parameters) {
        this.jobBasicInfo = jobBasicInfo;
        this.eventName = eventName;
        this.subject = subject;
        this.parameters = parameters;
    }

    public JobBasicInfo getJobBasicInfo() {
        return jobBasicInfo;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}

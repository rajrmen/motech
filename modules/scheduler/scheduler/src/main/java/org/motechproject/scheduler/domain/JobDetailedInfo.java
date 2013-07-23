package org.motechproject.scheduler.domain;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class JobDetailedInfo {
    private List<Map<String, Object> > eventInfoList;

    public JobDetailedInfo() {
        eventInfoList = new ArrayList<>();
    }

    public JobDetailedInfo(List<Map<String, Object> > eventInfoList) {
        this.eventInfoList = eventInfoList;
    }

    public List<Map<String, Object>> getEventInfoList() {
        return eventInfoList;
    }

    public void setEventInfoList(List<Map<String, Object>> eventInfoList) {
        this.eventInfoList = eventInfoList;
    }
}

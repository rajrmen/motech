package org.motechproject.scheduler.domain;

import java.util.ArrayList;
import java.util.List;

public class JobDetailedInfo {
    private List<EventInfo> eventInfoList;

    public JobDetailedInfo() {
        eventInfoList = new ArrayList<>();
    }

    public JobDetailedInfo(List<EventInfo> eventInfoList) {
        this.eventInfoList = eventInfoList;
    }

    public List<EventInfo> getEventInfoList() {
        return eventInfoList;
    }

    public void setEventInfoList(List<EventInfo> eventInfoList) {
        this.eventInfoList = eventInfoList;
    }
}

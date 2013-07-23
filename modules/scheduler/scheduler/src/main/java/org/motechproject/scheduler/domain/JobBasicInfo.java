package org.motechproject.scheduler.domain;

import org.joda.time.DateTime;

public class JobBasicInfo {
    private String activity;
    private String status;
    private String name;
    private DateTime startDate;
    private DateTime nextFireDate;
    private DateTime endDate;
    private String info;

    public JobBasicInfo() {

    }

    public JobBasicInfo(String activity, String status, String name, DateTime startDate, DateTime nextFireDate, DateTime endDate, String info) {
        this.activity = activity;
        this.status = status;
        this.name = name;
        this.startDate = startDate;
        this.nextFireDate = nextFireDate;
        this.endDate = endDate;
        this.info = info;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public DateTime getNextFireDate() {
        return nextFireDate;
    }

    public void setNextFireDate(DateTime nextFireDate) {
        this.nextFireDate = nextFireDate;
    }

    public DateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}

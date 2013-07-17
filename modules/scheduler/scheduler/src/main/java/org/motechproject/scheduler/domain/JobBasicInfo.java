package org.motechproject.scheduler.domain;

import java.util.Date;

public class JobBasicInfo {
    private String activity;
    private String name;
    private Date startDate;
    private Date nextFireDate;
    private Date endDate;
    private String info;

    public JobBasicInfo() {

    }

    public JobBasicInfo(String activity, String name, Date startDate, Date nextFireDate, Date endDate, String info) {
        this.activity = activity;
        this.name = name;
        this.startDate = startDate;
        this.nextFireDate = nextFireDate;
        this.endDate = endDate;
        this.info = info;
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getNextFireDate() {
        return nextFireDate;
    }

    public void setNextFireDate(Date nextFireDate) {
        this.nextFireDate = nextFireDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}

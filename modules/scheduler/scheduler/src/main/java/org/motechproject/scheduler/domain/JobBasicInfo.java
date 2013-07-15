package org.motechproject.scheduler.domain;

import java.util.Date;

public class JobBasicInfo {
    private String activity;
    private String status;
    private String name;
    private Date startDate;
    private String info;

    public JobBasicInfo() {

    }

    public JobBasicInfo(String activity, String status, String name, Date startDate, String info) {
        this.activity = activity;
        this.status = status;
        this.name = name;
        this.startDate = startDate;
        this.info = info;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}

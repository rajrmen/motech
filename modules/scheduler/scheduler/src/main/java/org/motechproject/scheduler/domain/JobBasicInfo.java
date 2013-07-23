package org.motechproject.scheduler.domain;

//

public class JobBasicInfo {
    private String activity;
    private String status;
    private String name;
    private String startDate;
    private String nextFireDate;
    private String endDate;
    private String info;

    public JobBasicInfo() {

    }

    public JobBasicInfo(String activity, String status, String name, String startDate, String nextFireDate, String endDate, String info) {
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getNextFireDate() {
        return nextFireDate;
    }

    public void setNextFireDate(String nextFireDate) {
        this.nextFireDate = nextFireDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}

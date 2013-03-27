package org.motechproject.sms.api.web;

public class GridSettings {

    private Integer rows;
    private Integer page;
    private String sortColumn;
    private String sortDirection;
    private String phoneNumber;
    private String message;
    private String timeFrom;
    private String timeTo;
    private Boolean inProgress;
    private Boolean delivered;
    private Boolean keepTrying;
    private Boolean aborted;
    private Boolean unknown;
    private Boolean inBound;
    private Boolean outBound;

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public String getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(String sortColumn) {
        this.sortColumn = sortColumn;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(String timeFrom) {
        this.timeFrom = timeFrom;
    }

    public String getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(String timeTo) {
        this.timeTo = timeTo;
    }

    public Boolean getInProgress() {
        return inProgress;
    }

    public void setInProgress(Boolean inProgress) {
        this.inProgress = inProgress;
    }

    public Boolean getDelivered() {
        return delivered;
    }

    public void setDelivered(Boolean delivered) {
        this.delivered = delivered;
    }

    public Boolean getKeepTrying() {
        return keepTrying;
    }

    public void setKeepTrying(Boolean keepTrying) {
        this.keepTrying = keepTrying;
    }

    public Boolean getAborted() {
        return aborted;
    }

    public void setAborted(Boolean aborted) {
        this.aborted = aborted;
    }

    public Boolean getUnknown() {
        return unknown;
    }

    public void setUnknown(Boolean unknown) {
        this.unknown = unknown;
    }

    public Boolean getInBound() {
        return inBound;
    }

    public void setInBound(Boolean inBound) {
        this.inBound = inBound;
    }

    public Boolean getOutBound() {
        return outBound;
    }

    public void setOutBound(Boolean outBound) {
        this.outBound = outBound;
    }
}

package org.motechproject.sms.api.web;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.commons.api.Range;
import org.motechproject.sms.api.DeliveryStatus;
import org.motechproject.sms.api.SMSType;
import org.motechproject.sms.api.service.SmsRecordSearchCriteria;

import java.util.ArrayList;
import java.util.List;

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

    public SmsRecordSearchCriteria toSmsRecordSearchCriteria() {
        List<SMSType> types = getSmsTypeFromSettings();
        List<DeliveryStatus> deliveryStatusList = getDeliveryStatusFromSettings();
        Range<DateTime> range = createRangeFromSettings();
        SmsRecordSearchCriteria criteria = new SmsRecordSearchCriteria();
        if (!types.isEmpty()) {
            criteria.withSmsTypes(types);
        }
        if (!deliveryStatusList.isEmpty()) {
            criteria.withDeliveryStatuses(deliveryStatusList);
        }
        if (StringUtils.isNotBlank(phoneNumber)) {
            criteria.withPhoneNumber(phoneNumber+"*");
        }
        if (StringUtils.isNotBlank(message)) {
            criteria.withMessageContent(message + "*");
        }
        criteria.withMessageTimeRange(range);
        return criteria;
    }

    private List<SMSType> getSmsTypeFromSettings() {
        List<SMSType> types = new ArrayList<>();
        if (inBound) {
            types.add(SMSType.INBOUND);
        }
        if (outBound) {
            types.add(SMSType.OUTBOUND);
        }
        return types;
    }

    private List<DeliveryStatus> getDeliveryStatusFromSettings() {
        List<DeliveryStatus> statusList = new ArrayList<>();
        if (inProgress) {
            statusList.add(DeliveryStatus.INPROGRESS);
        }
        if (delivered) {
            statusList.add(DeliveryStatus.DELIVERED);
        }
        if (keepTrying) {
            statusList.add(DeliveryStatus.KEEPTRYING);
        }
        if (aborted) {
            statusList.add(DeliveryStatus.ABORTED);
        }
        if (unknown) {
            statusList.add(DeliveryStatus.UNKNOWN);
        }
        return statusList;
    }

    private Range<DateTime> createRangeFromSettings() {
        DateTime from;
        DateTime to;
        if (StringUtils.isNotBlank(timeFrom)) {
             from = DateTime.parse(timeFrom);
        } else {
            from = new DateTime(0);
        }
        if (StringUtils.isNotBlank(timeTo)) {
            to = DateTime.parse(timeTo);
        } else {
            to = DateTime.now();
        }
        return new Range<DateTime>(from, to);
    }
}

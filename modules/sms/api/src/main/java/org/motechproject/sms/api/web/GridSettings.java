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
    private String deliveryStatues;
    private String directions;

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

    public String getDeliveryStatues() {
        return deliveryStatues;
    }

    public void setDeliveryStatues(String deliveryStatues) {
        this.deliveryStatues = deliveryStatues;
    }

    public String getDirections() {
        return directions;
    }

    public void setDirections(String directions) {
        this.directions = directions;
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
        return types;
    }

    private List<DeliveryStatus> getDeliveryStatusFromSettings() {
        List<DeliveryStatus> statusList = new ArrayList<>();
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

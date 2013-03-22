package org.motechproject.sms.api.service;

import org.joda.time.DateTime;
import org.motechproject.commons.api.Range;
import org.motechproject.sms.api.DeliveryStatus;
import org.motechproject.sms.api.SMSType;

import java.util.ArrayList;
import java.util.List;

//TODO: Builder?
public class SmsRecordSearchCriteria {

    private List<SMSType> smsTypes = new ArrayList<>();
    private String phoneNumber;
    private String messageContent;
    private Range<DateTime> messageTimeRange;
    private List<DeliveryStatus> deliveryStatuses = new ArrayList<>();
    private String referenceNumber;

    public SmsRecordSearchCriteria withSmsTypes(List<SMSType> smsTypes) {
        //TODO: Use java.util.set
        this.smsTypes.addAll(smsTypes);
        return this;
    }

    public SmsRecordSearchCriteria withPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public SmsRecordSearchCriteria withMessageContent(String messageContent) {
        this.messageContent = messageContent;
        return this;
    }

    public SmsRecordSearchCriteria withMessageTime(DateTime messageTime) {
        this.messageTimeRange = new Range<>(messageTime, messageTime);
        return this;
    }

    public SmsRecordSearchCriteria withMessageTimeRange(Range<DateTime> messageTimeRange) {
        this.messageTimeRange = messageTimeRange;
        return this;
    }

    public SmsRecordSearchCriteria withDeliveryStatuses(List<DeliveryStatus> deliveryStatuses) {
        //TODO: Use java.util.set
        this.deliveryStatuses.addAll(deliveryStatuses);
        return this;
    }

    public SmsRecordSearchCriteria withReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
        return this;
    }

    public List<String> getDeliveryStatuses() {
        return toStringList(deliveryStatuses);
    }

    public String getMessageContent() {
        return messageContent;
    }

    public Range<DateTime> getMessageTimeRange() {
        return messageTimeRange;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public List<String> getSmsTypes() {
        return toStringList(smsTypes);
    }

    private List<String> toStringList(List<? extends Enum> items) {
        List<String> itemStringList = new ArrayList<>();
        for (Enum item : items) {
            itemStringList.add(item.name());
        }
        return itemStringList;
    }
}

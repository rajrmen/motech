package org.motechproject.email.service;

import org.joda.time.DateTime;
import org.motechproject.commons.api.Range;
import org.motechproject.commons.couchdb.query.QueryParam;
import org.motechproject.email.DeliveryStatus;

import java.util.HashSet;
import java.util.Set;

public class EmailRecordSearchCriteria {

    private String fromAddress;
    private String toAddress;
    private String subject;
    private String message;
    private Range<DateTime> deliveryTimeRange;
    private Set<DeliveryStatus> deliveryStatuses = new HashSet<>();
    private String referenceNumber;
    private QueryParam queryParam = new QueryParam();

    public EmailRecordSearchCriteria withFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
        return this;
    }

    public EmailRecordSearchCriteria withToAddress(String toAddress) {
        this.toAddress = toAddress;
        return this;
    }

    public EmailRecordSearchCriteria withSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public EmailRecordSearchCriteria withMessage(String message) {
        this.message = message;
        return this;
    }

    public EmailRecordSearchCriteria withMessageTime(DateTime deliveryTimeRange) {
        this.deliveryTimeRange = new Range<>(deliveryTimeRange, deliveryTimeRange);
        return this;
    }

    public EmailRecordSearchCriteria withMessageTimeRange(Range<DateTime> deliveryTimeRange) {
        this.deliveryTimeRange = deliveryTimeRange;
        return this;
    }

    public EmailRecordSearchCriteria withDeliveryStatuses(Set<DeliveryStatus> deliveryStatuses) {
        this.deliveryStatuses.addAll(deliveryStatuses);
        return this;
    }

    public EmailRecordSearchCriteria withReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
        return this;
    }

    public EmailRecordSearchCriteria withQueryParam(QueryParam queryParam) {
        this.queryParam = queryParam;
        return this;
    }

    // Getters

    public String getFromAddress() {
        return fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }

    public Range<DateTime> getDeliveryTimeRange() {
        return deliveryTimeRange;
    }

    public Set<String> getDeliveryStatuses() {
        return toStringSet(deliveryStatuses);
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public QueryParam getQueryParam() {
        return queryParam;
    }

    private Set<String> toStringSet(Set<? extends Enum> items) {
        Set<String> itemStringSet = new HashSet<>();
        for (Enum item : items) {
            itemStringSet.add(item.name());
        }
        return itemStringSet;
    }

}

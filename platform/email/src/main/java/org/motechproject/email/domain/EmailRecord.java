package org.motechproject.email.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;
import org.motechproject.commons.date.util.DateUtil;

/**
 * The <code>EmailRecord</code> class represents a single Email record stored in CouchDB
 */

@TypeDiscriminator("doc.type === 'EmailRecord'")
public class EmailRecord extends MotechBaseDataObject {

    @JsonProperty
    private String fromAddress;
    @JsonProperty
    private String toAddress;
    @JsonProperty
    private String subject;
    @JsonProperty
    private String message;
    /**
     * Should be in UTC
     */
    @JsonProperty
    private String deliveryTime;
    @JsonProperty
    private DeliveryStatus deliveryStatus;

    public EmailRecord() {
    }

    public EmailRecord(String fromAddress, String toAddress, String subject, String message, DateTime deliveryTime, DeliveryStatus deliveryStatus) {
        super("EmailRecord");
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.subject = subject;
        this.message = message;
        this.deliveryTime = DateUtil.setTimeZoneUTC(deliveryTime).toString("Y-MM-dd HH:mm:ss");
        this.deliveryStatus = deliveryStatus;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setMessage(String message) {
        this.message = message;
    }

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

    public String getDeliveryTime() {
        return deliveryTime;
    }

    @JsonIgnore
    public DateTime getDeliveryTimeInDateTime() {
        return DateTime.parse(deliveryTime, DateTimeFormat.forPattern("Y-MM-dd HH:mm:ss"));
    }

    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

}

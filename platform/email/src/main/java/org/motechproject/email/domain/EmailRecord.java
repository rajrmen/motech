package org.motechproject.email.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;
import org.joda.time.DateTime;
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
    private DateTime deliveryTime;
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
        this.deliveryTime = deliveryTime;
        this.deliveryStatus = deliveryStatus;
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

    public DateTime getDeliveryTime() {
        return DateUtil.setTimeZoneUTC(deliveryTime);
    }

    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

}

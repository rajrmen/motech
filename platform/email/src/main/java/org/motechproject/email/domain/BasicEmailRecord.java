package org.motechproject.email.domain;

/**
 * The <code>BasicEmailRecord</code> class represents a single Email record, as it's seen by user
 * with basic email logging rights.
 */

public class BasicEmailRecord {

    private String deliveryTime;
    private DeliveryStatus deliveryStatus;

    public BasicEmailRecord(String time, DeliveryStatus status) {
        this.deliveryStatus = status;
        this.deliveryTime = time;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }
}

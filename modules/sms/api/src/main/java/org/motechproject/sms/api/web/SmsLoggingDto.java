package org.motechproject.sms.api.web;

import org.joda.time.DateTime;
import org.motechproject.sms.api.domain.SmsRecord;

public class SmsLoggingDto {

    private String phoneNumber;
    private String direction;
    private DateTime messageTime;
    private String status;
    private String message;

    public SmsLoggingDto(SmsRecord record) {
        this.phoneNumber = record.getPhoneNumber();
        this.direction = record.getSmsType().toString();
        this.messageTime = record.getMessageTime();
        this.status = record.getDeliveryStatus().toString();
        this.message = record.getMessageContent();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public DateTime getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(DateTime messageTime) {
        this.messageTime = messageTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

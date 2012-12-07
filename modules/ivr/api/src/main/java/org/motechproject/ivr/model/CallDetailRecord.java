package org.motechproject.ivr.model;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.ivr.event.CallEvent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.motechproject.commons.date.util.DateUtil.newDateTime;
import static org.motechproject.commons.date.util.DateUtil.setTimeZone;

/**
* Call Detail Record represents call events and data captured in a call along with call metrics.
*/
@TypeDiscriminator("doc.type === 'CallDetailRecord'")
public class CallDetailRecord  extends MotechBaseDataObject {
    /**
     * Call status
     */
    public enum Disposition {
        ANSWERED, BUSY, FAILED, NO_ANSWER, UNKNOWN;
    }

    private Date startDate;
    private Date endDate;
    private Date answerDate;
    private Disposition disposition;
    private Integer duration;
    private String message;
    private String phoneNumber;
    private CallDirection callDirection;
    private List<CallEvent> callEvents = new ArrayList<>();
    private String id;
    private String rev;

    private CallDetailRecord() {
    }

    /**
     * Constructor to create CallDetailRecord
     * @param startDate
     * @param endDate
     * @param answerDate
     * @param disposition
     * @param duration
     */
    public CallDetailRecord(Date startDate, Date endDate, Date answerDate,
                            Disposition disposition, Integer duration) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.answerDate = answerDate;
        this.disposition = disposition;
        this.duration = duration;
    }

    /**
     * CallDetailRecord constructor for failed calls
     * @param disposition: Status of call
     * @param message
     */
    public CallDetailRecord(Disposition disposition, String message) {
        this.message = message;
        this.disposition = disposition;
    }

    /**
     * Creates a call details record for given phone number and call details
     * @param phoneNumber: phone number of user.
     * @param callDirection: Incoming/outgoing
     * @param disposition: Call status (busy, failed etc)
     * @return
     */
    public static CallDetailRecord create(String phoneNumber, CallDirection callDirection, Disposition disposition) {
        CallDetailRecord callDetailRecord = new CallDetailRecord();
        callDetailRecord.startDate = DateUtil.now().toDate();
        callDetailRecord.disposition = disposition;
        callDetailRecord.answerDate = callDetailRecord.startDate;
        callDetailRecord.phoneNumber = phoneNumber;
        callDetailRecord.callDirection = callDirection;
        return callDetailRecord;
    }

    public Date getStartDate() {
        return startDate != null ? setTimeZone(newDateTime(startDate)).toDate() : startDate;
    }

    public Date getEndDate() {
        return endDate != null ? setTimeZone(newDateTime(endDate)).toDate() : endDate;
    }

    public Date getAnswerDate() {
        return answerDate != null ? setTimeZone(newDateTime(answerDate)).toDate() : answerDate;
    }

    public Disposition getDisposition() {
        return disposition;
    }

    public Integer getDuration() {
        return duration;
    }

    public String getMessage() {
        return message;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public CallDirection getCallDirection() {
        return callDirection;
    }


    public List<CallEvent> getCallEvents() {
        return callEvents;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setCallEvents(List<CallEvent> callEvents) {
        this.callEvents = callEvents;
    }

    public void setCallDirection(CallDirection callDirection) {
        this.callDirection = callDirection;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setAnswerDate(Date answerDate) {
        this.answerDate = answerDate;
    }

    public void setDisposition(Disposition disposition) {
        this.disposition = disposition;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Adds IVR events such as key press, hangup etc., to current call detail record.
     * @param callEvent
     */
    public void addCallEvent(CallEvent callEvent) {
        callEvents.add(callEvent);
    }

    /**
     * Get last call event for current call.
     * @return Call event
     */
    public CallEvent lastCallEvent() {
        return callEvents.size() == 0 ? null : callEvents.get(callEvents.size() - 1);
    }
}

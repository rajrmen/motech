package org.motechproject.ivr.service;

import org.motechproject.ivr.event.CallEvent;
import org.motechproject.ivr.model.CallDetailRecord;

import java.util.List;

public interface CallDetailService {

    CallDetailRecord createOrUpdate(CallDetailRecord record);
    CallDetailRecord find(String id);
    void addCallEvent(String callDetailRecordId, CallEvent callEvent);
    List<CallDetailRecord> getAll();
}

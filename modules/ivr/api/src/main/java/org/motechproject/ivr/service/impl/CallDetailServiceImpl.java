package org.motechproject.ivr.service.impl;

import org.motechproject.ivr.event.CallEvent;
import org.motechproject.ivr.model.CallDetailRecord;
import org.motechproject.ivr.repository.AllCallDetailRecords;
import org.motechproject.ivr.service.CallDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CallDetailServiceImpl implements CallDetailService {

    private AllCallDetailRecords allCallDetailRecords;

    @Autowired
    public CallDetailServiceImpl(AllCallDetailRecords allCallDetailRecords) {
        this.allCallDetailRecords = allCallDetailRecords;
    }

    @Override
    public CallDetailRecord createOrUpdate(CallDetailRecord record) {
        return allCallDetailRecords.createOrUpdate(record);
    }

    @Override
    public CallDetailRecord find(String id) {
        return allCallDetailRecords.get(id);
    }

    @Override
    public void addCallEvent(String callDetailRecordId, CallEvent callEvent) {
        CallDetailRecord callDetailRecord = allCallDetailRecords.get(callDetailRecordId);
        if (callDetailRecord != null) {
            callDetailRecord.addCallEvent(callEvent);
            allCallDetailRecords.update(callDetailRecord);
        }
    }

}

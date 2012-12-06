package org.motechproject.ivr.service;

import org.motechproject.ivr.model.CallDetailRecord;

public interface CallDetailService {

    public CallDetailRecord createOrUpdate(CallDetailRecord record);
    public CallDetailRecord find(String id);

}

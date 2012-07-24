package org.motechproject.ivr.service.impl;

import org.motechproject.ivr.domain.FlowSession;
import org.motechproject.ivr.domain.FlowSessionImpl;
import org.motechproject.ivr.repository.AllCallSessionRecords;
import org.motechproject.ivr.service.IVRSessionManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IVRSessionManagementServiceImpl implements IVRSessionManagementService {

    private AllCallSessionRecords allCallSessionRecords;

    @Autowired
    public IVRSessionManagementServiceImpl(AllCallSessionRecords allCallSessionRecords) {
        this.allCallSessionRecords = allCallSessionRecords;
    }

    @Override
    public FlowSession getCallSession(String sessionId) {
        return allCallSessionRecords.findOrCreate(sessionId);
    }

    @Override
    public void updateCallSession(FlowSession flowSession) {
        if (flowSession instanceof FlowSessionImpl){
            allCallSessionRecords.update((FlowSessionImpl)flowSession);
        }
    }

    @Override
    public void removeCallSession(String sessionId) {
        FlowSession flowSession = allCallSessionRecords.findBySessionId(sessionId);
        if(flowSession != null && flowSession instanceof FlowSessionImpl) {
            allCallSessionRecords.remove((FlowSessionImpl)flowSession);
        }
    }

    @Override
    public boolean isValidSession(String sessionId) {
        return allCallSessionRecords.findBySessionId(sessionId) != null;
    }
}

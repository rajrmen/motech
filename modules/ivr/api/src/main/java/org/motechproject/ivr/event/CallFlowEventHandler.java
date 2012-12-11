package org.motechproject.ivr.event;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.ivr.service.CallDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CallFlowEventHandler {

    private CallDetailService callDetailService;

    @Autowired
    public CallFlowEventHandler(CallDetailService callDetailService) {
        this.callDetailService = callDetailService;
    }

    @MotechListener(subjects = {CallEvent.SUBJECT} )
    public void handle(MotechEvent event) {
        CallEvent callEvent = (CallEvent) event.getParameters().get(CallEvent.KEY);
        String callDetailRecordId = (String) event.getParameters().get(CallEvent.CALL_DETAIL_RECORD_ID_KEY);

        callDetailService.addCallEvent(callDetailRecordId, callEvent);
    }
}

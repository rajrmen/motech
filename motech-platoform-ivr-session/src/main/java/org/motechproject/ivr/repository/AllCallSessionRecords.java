package org.motechproject.ivr.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.ivr.domain.FlowSession;
import org.motechproject.ivr.domain.FlowSessionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllCallSessionRecords extends MotechBaseRepository<FlowSessionImpl> {
    @Autowired
    protected AllCallSessionRecords(@Qualifier("platformIVRDbConnector") CouchDbConnector db) {
        super(FlowSessionImpl.class, db);
    }

    @View(name = "by_session_id", map = "function(doc) { emit(doc.sessionId); }")
    public FlowSessionImpl findBySessionId(String sessionId) {
        return singleResult(queryView("by_session_id", sessionId.toUpperCase()));
    }

    public FlowSessionImpl findOrCreate(String sessionId) {
        FlowSessionImpl flowSession = findBySessionId(sessionId);
        if (flowSession == null) {
            flowSession = new FlowSessionImpl(sessionId.toUpperCase());
            add(flowSession);
        }
        return flowSession;
    }
}

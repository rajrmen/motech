package org.motechproject.ivr.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.ivr.model.CallDetailRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllCallDetailRecords extends MotechBaseRepository<CallDetailRecord> {

    @Autowired
    protected AllCallDetailRecords(@Qualifier("platformIVRDbConnector") CouchDbConnector db) {
        super(CallDetailRecord.class, db);
    }

    public CallDetailRecord createOrUpdate(CallDetailRecord record) {
        if(record.getId() != null && get(record.getId()) != null) {
            update(record);
            return record;
        }
        add(record);
        return record;
    }
}

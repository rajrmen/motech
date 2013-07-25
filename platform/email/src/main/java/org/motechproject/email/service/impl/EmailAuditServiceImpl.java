package org.motechproject.email.service.impl;

import org.motechproject.email.domain.EmailRecord;
import org.motechproject.email.domain.EmailRecords;
import org.motechproject.email.repository.AllEmailRecords;
import org.motechproject.email.service.EmailAuditService;
import org.motechproject.email.service.EmailRecordSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("emailAuditService")
public class EmailAuditServiceImpl implements EmailAuditService {

    private AllEmailRecords allEmailRecords;

    @Autowired
    public EmailAuditServiceImpl(AllEmailRecords allEmailRecords) {
        this.allEmailRecords = allEmailRecords;
    }

    @Override
    public void log(EmailRecord emailRecord) {
        allEmailRecords.addOrReplace(emailRecord);
    }

    @Override
    public List<EmailRecord> findAllEmailRecords() {
        return allEmailRecords.getAll();
    }

    @Override
    public EmailRecords findAllEmailRecords(EmailRecordSearchCriteria criteria) {
        return allEmailRecords.findAllBy(criteria);
    }
}

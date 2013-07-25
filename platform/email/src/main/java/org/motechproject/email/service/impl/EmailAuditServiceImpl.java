package org.motechproject.email.service.impl;

import org.motechproject.email.domain.EmailRecord;
import org.motechproject.email.domain.EmailRecords;
import org.motechproject.email.repository.AllEmailRecords;
import org.motechproject.email.service.EmailAuditService;
import org.motechproject.email.service.EmailRecordSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The <code>EmailAuditServiceImpl</code> class provides API for everything connected with logging e-mails
 * and searching through them
 */

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
    public EmailRecords findEmailRecords(EmailRecordSearchCriteria criteria) {
        return allEmailRecords.findAllBy(criteria);
    }
}

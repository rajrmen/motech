package org.motechproject.email.service;

import org.motechproject.email.domain.EmailRecord;
import org.motechproject.email.domain.EmailRecords;

import java.util.List;

public interface EmailAuditService {

    void log(EmailRecord mailRecord);

    List<EmailRecord> findAllEmailRecords();

    EmailRecords findAllEmailRecords(EmailRecordSearchCriteria criteria);

}
package org.motechproject.email.service;

import org.motechproject.email.domain.EmailRecord;

import java.util.List;

public interface EmailAuditService {

    void log(EmailRecord mailRecord);

    List<EmailRecord> findAllEmailRecords();

    List<EmailRecord> findEmailRecords(EmailRecordSearchCriteria criteria);

    void delete(EmailRecord emailRecord);
}
package org.motechproject.sms.api.service;

import org.motechproject.sms.api.domain.SmsRecord;
import org.motechproject.sms.api.web.SmsRecords;

import java.util.List;

public interface SmsAuditService {

    void log(SmsRecord smsRecord);

    void updateDeliveryStatus(String recipient, String refNo, String name);

    List<SmsRecord> findAllSmsRecords();

    SmsRecords findAllSmsRecords(SmsRecordSearchCriteria criteria, int page, int pageSize, String sortBy, boolean reverse);
}

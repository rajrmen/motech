package org.motechproject.email.service.impl;

import org.motechproject.email.domain.EmailRecord;
import org.motechproject.email.repository.AllEmailRecords;
import org.motechproject.email.service.EmailAuditService;
import org.motechproject.email.service.EmailRecordSearchCriteria;
import org.motechproject.security.service.MotechRoleService;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * The <code>EmailAuditServiceImpl</code> class provides API for everything connected with logging e-mails
 * and searching through them
 */

@Service("emailAuditService")
public class EmailAuditServiceImpl implements EmailAuditService {

    private static final String EMAIL_LOG_BODY = "mail.log.body";
    private static final String EMAIL_LOG_ADDRESS = "mail.log.address";
    private static final String EMAIL_LOG_SUBJECT = "mail.log.subject";

    private static final String FALSE = "false";

    private AllEmailRecords allEmailRecords;
    private SettingsFacade settings;

    @Autowired
    private MotechUserService motechUserService;

    @Autowired
    private MotechRoleService motechRoleService;

    @Autowired
    public EmailAuditServiceImpl(AllEmailRecords allEmailRecords, @Qualifier("emailSettings") SettingsFacade settings) {
        this.allEmailRecords = allEmailRecords;
        this.settings = settings;
    }

    @Override
    public void log(EmailRecord emailRecord) {
        if (settings.getProperty(EMAIL_LOG_BODY).equals(FALSE)) {
            emailRecord.setMessage("");
        }

        if (settings.getProperty(EMAIL_LOG_ADDRESS).equals(FALSE)) {
            emailRecord.setFromAddress("");
            emailRecord.setToAddress("");
        }

        if (settings.getProperty(EMAIL_LOG_SUBJECT).equals(FALSE)) {
            emailRecord.setSubject("");
        }

        allEmailRecords.addOrReplace(emailRecord);
    }

    @Override
    public List<EmailRecord> findAllEmailRecords() {
        return hideColumnsIfNoCredentials(allEmailRecords.getAll());
    }

    @Override
    public void delete(EmailRecord emailRecord) {
        allEmailRecords.delete(emailRecord);
    }

    @Override
    public List<EmailRecord> findEmailRecords(EmailRecordSearchCriteria criteria) {
        return hideColumnsIfNoCredentials(allEmailRecords.findAllBy(criteria));
    }

    private List<EmailRecord> hideColumnsIfNoCredentials(List<EmailRecord> records) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = motechUserService.getRoles(auth.getName());

        boolean viewBasicLogs = false;
        boolean viewDetailedLogs= false;

        for (String role : roles) {
            List<String> permissions = motechRoleService.getRole(role).getPermissionNames();
            if (permissions.contains("viewBasicEmailLogs")) {
                viewBasicLogs = true;
            }
            if (permissions.contains("viewDetailedEmailLogs")) {
                viewDetailedLogs = true;
            }
        }

        if (viewDetailedLogs) {
            return records;
        } else if (viewBasicLogs) {
            List<EmailRecord> basicRecords = new ArrayList<>();
            for (EmailRecord record : records) {
                record.setToAddress("");
                record.setFromAddress("");
                record.setMessage("");
                record.setSubject("");
                basicRecords.add(record);
            }
            return basicRecords;
        } else {
            return new ArrayList<>();
        }
    }
}

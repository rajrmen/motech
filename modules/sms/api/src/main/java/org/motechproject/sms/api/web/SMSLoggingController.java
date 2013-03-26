package org.motechproject.sms.api.web;

import org.motechproject.sms.api.domain.SmsRecord;
import org.motechproject.sms.api.service.SmsAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class SMSLoggingController {

    @Autowired
    private SmsAuditService smsAuditService;

    @RequestMapping(value = "/smslogging")
    public List<SmsRecord> getSmsRecords() {
        return smsAuditService.findAllSmsRecords();
    }

}

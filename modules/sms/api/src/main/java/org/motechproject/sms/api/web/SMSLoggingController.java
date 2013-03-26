package org.motechproject.sms.api.web;

import org.motechproject.sms.api.domain.SmsRecord;
import org.motechproject.sms.api.service.SmsAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class SMSLoggingController {

    @Autowired
    private SmsAuditService smsAuditService;

    @RequestMapping(value = "/smslogging", method = RequestMethod.GET)
    public List<SmsRecord> getSmsRecords(final @RequestParam Integer rows,
                                         final @RequestParam Integer page,
                                         final @RequestParam(value = "sidx") String sortColumn,
                                         final @RequestParam(value = "sord") String sortDirection) {
        List<SmsRecord> records = smsAuditService.findAllSmsRecords();
        return smsAuditService.findAllSmsRecords();
    }

}

package org.motechproject.sms.api.web;

import org.motechproject.sms.api.service.SmsAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;


@Controller
public class SMSLoggingController {

    @Autowired
    private SmsAuditService smsAuditService;

    /*@RequestMapping(value = "/smslogging", method = RequestMethod.GET)
    public SmsLoggingRecords getSmsRecords(GridSettings settings) {
        SmsRecordSearchCriteria criteria = settings.toSmsRecordSearchCriteria();
        boolean reverse = "desc".equalsIgnoreCase(settings.getSortDirection());
        SmsRecords smsRecords = smsAuditService.findAllSmsRecords(criteria, settings.getPage(), settings.getRows(), settings.getSortColumn(), reverse);

        //return new SmsLoggingRecords(settings.getPage(), settings.getRows(), smsRecords);
        return null;
    }*/

}

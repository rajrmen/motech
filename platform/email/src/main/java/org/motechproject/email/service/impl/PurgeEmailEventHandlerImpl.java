package org.motechproject.email.service.impl;

import org.joda.time.DateTime;
import org.motechproject.email.domain.EmailRecord;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

/**
 * The <code>PurgeEmailEventHandlerImpl</code> class is responsible for handling events,
 * connected with purging {@Link EmailRecord}s
 */

@Service
public class PurgeEmailEventHandlerImpl {

    @Autowired
    private EmailAuditServiceImpl auditService;

    @MotechListener (subjects = { "Purge Mails" } )
    public void handle(MotechEvent event) {
        String purgeTime = (String) event.getParameters().get("purgeTime");
        String purgeMultiplier = (String) event.getParameters().get("purgeMultiplier");

        DateTime deadline;

        if (purgeMultiplier.equals("hours")) {
            deadline = DateTime.now().minusHours(Integer.parseInt(purgeTime));
        } else if (purgeMultiplier.equals("days")) {
            deadline = DateTime.now().minusDays(Integer.parseInt(purgeTime));
        } else if (purgeMultiplier.equals("weeks")) {
            deadline = DateTime.now().minusWeeks(Integer.parseInt(purgeTime));
        } else if (purgeMultiplier.equals("months")) {
            deadline = DateTime.now().minusMonths(Integer.parseInt(purgeTime));
        } else {
            deadline = DateTime.now().minusYears(Integer.parseInt(purgeTime));
        }

        List<EmailRecord> emailRecordList = auditService.findAllEmailRecords();
        Iterator<EmailRecord> it = emailRecordList.iterator();

        while (it.hasNext()) {
            EmailRecord mail = it.next();
            if (mail.getDeliveryTimeInDateTime().isBefore(deadline)) {
                auditService.delete(mail);
            }
        }
    }

}

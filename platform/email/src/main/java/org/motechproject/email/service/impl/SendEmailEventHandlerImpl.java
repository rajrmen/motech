package org.motechproject.email.service.impl;

import org.motechproject.email.constants.SendEmailConstants;
import org.motechproject.email.domain.DeliveryStatus;
import org.motechproject.email.domain.EmailRecord;
import org.motechproject.email.model.Mail;
import org.motechproject.email.service.EmailAuditService;
import org.motechproject.email.service.EmailSenderService;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import static org.motechproject.commons.date.util.DateUtil.now;

/**
 * The <code>SendEmailEventHandlerImpl</code> class is responsible for listening to and handling events
 * connected with sending e-mails
 */

@Service
public class SendEmailEventHandlerImpl {

    private EmailSenderService emailSenderService;
    private EmailAuditService emailAuditService;

    @Autowired
    public SendEmailEventHandlerImpl(EmailSenderService emailSenderService, EmailAuditService emailAuditService) {
        this.emailSenderService = emailSenderService;
        this.emailAuditService = emailAuditService;
    }

    @MotechListener (subjects = { SendEmailConstants.SEND_EMAIL_SUBJECT })
    public void handle(MotechEvent event) {
        String fromAddress = (String) event.getParameters().get(SendEmailConstants.FROM_ADDRESS);
        String toAddress = (String) event.getParameters().get(SendEmailConstants.TO_ADDRESS);
        String subject = (String) event.getParameters().get(SendEmailConstants.SUBJECT);
        String message = (String) event.getParameters().get(SendEmailConstants.MESSAGE);

        try {
            emailSenderService.send(new Mail(fromAddress, toAddress, subject, message));
            emailAuditService.log(new EmailRecord(
                    fromAddress, toAddress, subject, message,
                    now(), DeliveryStatus.SENT));
        } catch (MailException me) {
            emailAuditService.log(new EmailRecord(
                    fromAddress, toAddress, subject, message,
                    now(), DeliveryStatus.ERROR));
        }
    }
}

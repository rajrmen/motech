package org.motechproject.email.service.impl;

import org.motechproject.email.constants.SendEmailConstants;
import org.motechproject.email.domain.EmailRecord;
import org.motechproject.email.model.Mail;
import org.motechproject.email.service.EmailAuditService;
import org.motechproject.email.service.EmailSenderService;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.motechproject.email.DeliveryStatus;

import java.util.Random;

import static org.motechproject.commons.date.util.DateUtil.now;

@Service
public class SendEmailEventHandlerImpl {

    private EmailSenderService emailSenderService;
    private EmailAuditService emailAuditService;

    @Autowired
    public SendEmailEventHandlerImpl(EmailSenderService emailSenderService, EmailAuditService emailAuditService) {
        this.emailSenderService = emailSenderService;
        this.emailAuditService = emailAuditService;
    }

    private Random random = new Random();

    @MotechListener (subjects = { SendEmailConstants.SEND_EMAIL_SUBJECT })
    public void handle(MotechEvent event) {
        String fromAddress = (String) event.getParameters().get(SendEmailConstants.FROM_ADDRESS);
        String toAddress = (String) event.getParameters().get(SendEmailConstants.TO_ADDRESS);
        String subject = (String) event.getParameters().get(SendEmailConstants.SUBJECT);
        String message = (String) event.getParameters().get(SendEmailConstants.MESSAGE);
        emailAuditService.log(new EmailRecord(
                fromAddress, toAddress, subject, message,
                now(), DeliveryStatus.PENDING, Integer.toString(Math.abs(random.nextInt()))));

        emailSenderService.send(new Mail(fromAddress, toAddress, subject, message));
    }
}

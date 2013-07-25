package org.motechproject.email.web;

import org.motechproject.email.DeliveryStatus;
import org.motechproject.email.domain.EmailRecord;
import org.motechproject.email.model.Mail;
import org.motechproject.email.service.EmailAuditService;
import org.motechproject.email.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.util.Random;

import static org.motechproject.commons.date.util.DateUtil.now;

@Controller
public class SendEmailController {
    private EmailSenderService senderService;
    private EmailAuditService auditService;

    @Autowired
    public SendEmailController(EmailSenderService senderService, EmailAuditService auditService) {
        this.senderService = senderService;
        this.auditService = auditService;
    }

    private Random random = new Random();

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void sendEmail(@RequestBody Mail mail) {
        auditService.log(new EmailRecord(
                mail.getFromAddress(), mail.getToAddress(), mail.getSubject(), mail.getMessage(),
                now(), DeliveryStatus.PENDING, Integer.toString(Math.abs(random.nextInt()))));
        senderService.send(mail);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleException(Exception e) throws IOException {
        return e.getMessage();
    }
}
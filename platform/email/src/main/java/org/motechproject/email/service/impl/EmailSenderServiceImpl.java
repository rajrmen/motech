package org.motechproject.email.service.impl;

import org.apache.log4j.Logger;
import org.motechproject.email.model.Mail;
import org.motechproject.email.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * The <class>EmailSenderServiceImpl</class> class provides API for sending e-mails
 */

@Service("emailSenderService")
public class EmailSenderServiceImpl implements EmailSenderService {

    @Autowired
    private JavaMailSender mailSender;

    private static final Logger LOG = Logger.getLogger(EmailSenderServiceImpl.class);

    @Override
    public void send(final Mail mail) {
        LOG.info(String.format("Sending message [%s] from [%s] to [%s] with subject [%s].",
                mail.getMessage(), mail.getFromAddress(), mail.getToAddress(), mail.getSubject()));
        mailSender.send(getMimeMessagePreparator(mail));
    }

    MotechMimeMessagePreparator getMimeMessagePreparator(Mail mail) {
        return new MotechMimeMessagePreparator(mail);
    }
}

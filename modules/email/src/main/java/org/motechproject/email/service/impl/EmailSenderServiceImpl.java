package org.motechproject.email.service.impl;

import org.motechproject.email.model.Mail;
import org.motechproject.email.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service("emailSenderService")
public class EmailSenderServiceImpl implements EmailSenderService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void send(final Mail mail) {
        ClassLoader tcl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(javax.mail.Session.class.getClassLoader());
        mailSender.send(getMimeMessagePreparator(mail));
        Thread.currentThread().setContextClassLoader(tcl);
    }

    MotechMimeMessagePreparator getMimeMessagePreparator(Mail mail) {
        return new MotechMimeMessagePreparator(mail);
    }
}
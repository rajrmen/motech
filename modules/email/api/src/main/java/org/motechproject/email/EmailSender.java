package org.motechproject.email;


import org.motechproject.email.model.MailDetail;

public interface EmailSender {

    void sendCriticalNotificationEmail(String address, MailDetail message);
}

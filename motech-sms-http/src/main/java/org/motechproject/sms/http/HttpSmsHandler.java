package org.motechproject.sms.http;

import org.motechproject.sms.api.service.SmsService;
import org.motechproject.sms.http.service.SmsHttpService;
import org.motechproject.sms.http.template.SmsHttpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HttpSmsHandler {

    private SmsHttpTemplate template;
    private SmsService smsService;
    private SmsHttpService smsHttpService;

    @Autowired
    public HttpSmsHandler(SmsService smsService, SmsHttpService smsHttpService, TemplateReader templateReader) {
        this.smsService = smsService;
        this.smsHttpService = smsHttpService;
        this.template = templateReader.getTemplate();
    }


    public void handle(List<String> recipients, String message) throws SmsDeliveryFailureException {
        if (template.isMultiRecipientSupported() || recipients.size() == 1) {
            smsHttpService.sendSms(recipients, message);
        } else
            createSendSmsEvents(recipients, message);
    }

    private void createSendSmsEvents(List<String> recipients, String message) {
        for (String recipient : recipients) {
            smsService.sendSMS(recipient, message);
        }
    }
}

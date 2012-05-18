package org.motechproject.sms.http.service;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.drools.core.util.StringUtils;
import org.motechproject.sms.http.SmsDeliveryFailureException;
import org.motechproject.sms.http.template.SmsHttpTemplate;
import org.motechproject.sms.http.TemplateReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;

@Component
public class SmsHttpService {

    private SmsHttpTemplate template;
    private HttpClient commonsHttpClient;
    private static Logger log = LoggerFactory.getLogger(SmsHttpService.class);

    @Autowired
    public SmsHttpService(TemplateReader templateReader, HttpClient commonsHttpClient) {
        String templateFile = "/sms-http-template.json";
        this.template = templateReader.getTemplate(templateFile);
        this.commonsHttpClient = commonsHttpClient;
    }

    public void sendSms(List<String> recipients, String message) throws SmsDeliveryFailureException {
        if (CollectionUtils.isEmpty(recipients) || StringUtils.isEmpty(message))
            throw new IllegalArgumentException("Recipients or Message should not be empty");

        String response;
        HttpMethod httpMethod = null;
        try {
            httpMethod = template.generateRequestFor(recipients, message);
            int status = commonsHttpClient.executeMethod(httpMethod);
            response = httpMethod.getResponseBodyAsString();
            log.info("HTTP Status:" + status + "|Response:" + response);
        } catch (Exception e) {
            log.debug("SMSDeliveryFailure due to : ", e);
            throw new SmsDeliveryFailureException(e);
        } finally {
            if (httpMethod != null) httpMethod.releaseConnection();
        }

        if (response == null || !response.toLowerCase().contains(template.getOutgoing().getResponse().getSuccess().toLowerCase())) {
            log.info("SMSDeliveryFailed retrying...");
            throw new SmsDeliveryFailureException();
        }
    }
}

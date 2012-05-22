package org.motechproject.sms.http;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.sms.api.service.SmsService;
import org.motechproject.sms.api.service.SmsServiceImpl;
import org.motechproject.sms.http.service.SmsHttpService;
import org.motechproject.sms.http.template.SmsHttpTemplate;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class HttpSmsHandlerTest {

    private SmsHttpService smsHttpService;
    private TemplateReader templateReader;
    private SmsHttpTemplate template;
    private SmsService smsService;
    private List<String> recipients;
    private String message;

    @Before
    public void setUp(){
        smsHttpService = mock(SmsHttpService.class);
        smsService = mock(SmsService.class);
        templateReader = mock(TemplateReader.class);
        template = mock(SmsHttpTemplate.class);

        when(templateReader.getTemplate()).thenReturn(template);

        recipients = Arrays.asList("111", "222");
        message = "some Message";
    }

    @Test
    public void shouldSendSms_WhenMultiRecipientIsSupported() throws SmsDeliveryFailureException {
        when(template.isMultiRecipientSupported()).thenReturn(true);
        HttpSmsHandler httpSmsHandler = new HttpSmsHandler(smsService, smsHttpService, templateReader);

        httpSmsHandler.handle(recipients, message);

        verify(smsHttpService).sendSms(recipients, message);
    }

    @Test
    public void shouldCreateMultipleEvents_WhenMultiRecipientIsNotSupported() throws SmsDeliveryFailureException {
        when(template.isMultiRecipientSupported()).thenReturn(false);
        HttpSmsHandler httpSmsHandler = new HttpSmsHandler(smsService, smsHttpService, templateReader);

        httpSmsHandler.handle(recipients, message);

        verify(smsHttpService, never()).sendSms(recipients, message);
        verify(smsService).sendSMS(recipients.get(0), message);
        verify(smsService).sendSMS(recipients.get(1), message);

    }

    @Test
    public void shouldNotCreateMultipleEvents_WhenMultiRecipientIsNotSupported_ForSingleRecipient() throws SmsDeliveryFailureException {
        when(template.isMultiRecipientSupported()).thenReturn(false);
        HttpSmsHandler httpSmsHandler = new HttpSmsHandler(smsService, smsHttpService, templateReader);

        List<String> recipients = Arrays.asList("singleRecipient");

        httpSmsHandler.handle(recipients, message);

        verify(smsHttpService).sendSms(recipients, message);
        verify(smsService, never()).sendSMS(recipients.get(0), message);
    }
}

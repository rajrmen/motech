package org.motechproject.sms.http;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.model.RunOnceSchedulableJob;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.sms.api.constants.EventDataKeys;
import org.motechproject.sms.http.constants.EventSubjects;

import org.motechproject.sms.http.service.SmsHttpService;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

public class ScheduledSmsSendHandlerTest {

    @Mock
    private SmsHttpService smsHttpService;
    @Mock
    private MotechSchedulerService motechSchedulerService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldListenToSmsSendEvent() throws NoSuchMethodException {
        Method handleMethod = ScheduledSmsSendHandler.class.getDeclaredMethod("handle", new Class[]{MotechEvent.class});
        assertTrue("MotechListener annotation missing", handleMethod.isAnnotationPresent(MotechListener.class));
        MotechListener annotation = handleMethod.getAnnotation(MotechListener.class);
        assertArrayEquals(new String[]{EventSubjects.SCHEDULED_SEND_SMS}, annotation.subjects());
    }

    @Test
    public void shouldSendSmsBasedOnEventDetails() throws SmsDeliveryFailureException {
        HashMap<String, Object> eventParameters = new HashMap<String, Object>();
        List<String> recipients = asList(new String[]{"recipient_1", "recipient_2"});
        String messageText = "message_text";

        eventParameters.put(EventDataKeys.RECIPIENTS, recipients);
        eventParameters.put(EventDataKeys.MESSAGE, messageText);

        MotechEvent motechEvent = new MotechEvent(EventSubjects.SCHEDULED_SEND_SMS, eventParameters);

        new ScheduledSmsSendHandler(smsHttpService).handle(motechEvent);
        verify(smsHttpService).sendSms(recipients, messageText);
    }
    
    @Test
    public void shouldRaiseScheduledSmsWhenDeliveryTimeIsSpecified() throws SmsDeliveryFailureException {
        HashMap<String, Object> eventParameters = new HashMap<String, Object>();
        List<String> recipients = asList(new String[]{"recipient_1", "recipient_2"});
        String messageText = "message_text";
        DateTime deliveryTime = DateTime.now();

        eventParameters.put(EventDataKeys.RECIPIENTS, recipients);
        eventParameters.put(EventDataKeys.MESSAGE, messageText);
        eventParameters.put(EventDataKeys.DELIVERY_TIME, deliveryTime);

        MotechEvent motechEvent = new MotechEvent(org.motechproject.sms.api.constants.EventSubjects.SEND_SMS, eventParameters);

        new SmsSendHandler(smsHttpService, motechSchedulerService).handle(motechEvent);

        verifyZeroInteractions(smsHttpService);
        ArgumentCaptor<RunOnceSchedulableJob> jobCaptor = ArgumentCaptor.forClass(RunOnceSchedulableJob.class);
        verify(motechSchedulerService).safeScheduleRunOnceJob(jobCaptor.capture());

        RunOnceSchedulableJob job = jobCaptor.getValue();
        assertEquals(EventSubjects.SCHEDULED_SEND_SMS, job.getMotechEvent().getSubject());
    }

}

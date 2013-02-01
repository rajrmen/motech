package org.motechproject.sms.api.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.event.listener.EventListenerRegistry;
import org.motechproject.sms.api.constants.EventDataKeys;
import org.motechproject.sms.api.constants.EventSubjects;
import org.motechproject.sms.api.exceptions.SendSmsException;
import org.motechproject.testing.utils.TestEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.motechproject.commons.date.util.DateUtil.newDateTime;
import static org.motechproject.testing.utils.TimeFaker.fakeNow;
import static org.motechproject.testing.utils.TimeFaker.stopFakingTime;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/META-INF/motech/*.xml"})
public class SmsServiceImplIT {

    @Autowired
    SmsService smsService;

    @Autowired
    EventListenerRegistry eventListenerRegistry;

    @Test
    public void shouldRaiseSendSmsEvent() throws InterruptedException {
        try {
            fakeNow(newDateTime(2010, 10, 1));
            TestEventListener listener = new TestEventListener("listener");
            eventListenerRegistry.registerListener(listener, EventSubjects.SEND_SMS);
            smsService.sendSMS(new SendSmsRequest(asList("123"), "hello", newDateTime(2010, 10, 5)));
            synchronized (listener.getReceivedEvents()) {
                while (listener.getReceivedEvents().size() == 0) {
                    listener.getReceivedEvents().wait(2000);
                }
            }
            assertEquals(1, listener.getReceivedEvents().size());
            assertEquals(asList("123"), listener.getReceivedEvents().get(0).getParameters().get(EventDataKeys.RECIPIENTS));
            assertEquals("hello", listener.getReceivedEvents().get(0).getParameters().get(EventDataKeys.MESSAGE));
            assertEquals(newDateTime(2010, 10, 5), listener.getReceivedEvents().get(0).getParameters().get(EventDataKeys.DELIVERY_TIME));
        } finally {
            eventListenerRegistry.clearListenersForBean("listener");
            stopFakingTime();
        }
    }

    @Test(expected = SendSmsException.class)
    public void shouldThrowExceptionForMissingRecipient() throws InterruptedException {
        try {
            TestEventListener listener = new TestEventListener("listener");
            eventListenerRegistry.registerListener(listener, EventSubjects.SEND_SMS);
            smsService.sendSMS(new SendSmsRequest(null, "hello"));
            synchronized (listener.getReceivedEvents()) {
                listener.getReceivedEvents().wait(2000);
            }
            assertEquals(0, listener.getReceivedEvents().size());
        } finally {
            eventListenerRegistry.clearListenersForBean("listener");
        }
    }

}

package org.motechproject.email.repository;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.email.DeliveryStatus;
import org.motechproject.email.domain.EmailRecord;
import org.motechproject.email.domain.EmailRecords;
import org.motechproject.email.service.EmailRecordSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testApplicationEmail.xml"})
public class AllEmailRecordsTest {

    @Autowired
    private AllEmailRecords allEmailRecords;

    @Test
    public void shouldCreateEmail() {
        DeliveryStatus deliveryStatus = DeliveryStatus.PENDING;
        String refNo = "refNo";
        String fromAddress = "f@adr";
        String toAddress = "t@adr";
        String subject = "test-subject";
        String message = "test-message";
        DateTime sentDate = DateUtil.now();

        EmailRecord emailRecord = new EmailRecord(fromAddress, toAddress, subject, message, sentDate, deliveryStatus, refNo);
        allEmailRecords.addOrReplace(emailRecord);

        EmailRecord savedMessage = allEmailRecords.findLatestBy(toAddress, refNo);
        assertNotNull(savedMessage);
        assertEquals(savedMessage.getSubject(), subject);
        assertEquals(savedMessage.getMessage(), message);
    }

    @Test
    public void shouldFindLatestEmailForDuplicateRecords() {
        String refNo = "refNo";
        String refNo2 = "refNo2";
        String fromAddress = "f@adr";
        String toAddress = "t@adr";
        String subject = "test-subject";
        String message = "test-message";
        DateTime messageTime = DateUtil.now().toDateTime(DateTimeZone.UTC);

        allEmailRecords.addOrReplace(new EmailRecord(fromAddress, toAddress, subject, message, messageTime, DeliveryStatus.PENDING, refNo));
        allEmailRecords.addOrReplace(new EmailRecord(fromAddress, toAddress, subject, message, messageTime.plusDays(1), DeliveryStatus.FAILURE, refNo));
        allEmailRecords.addOrReplace(new EmailRecord(fromAddress, toAddress, subject, message, messageTime.plusDays(3), DeliveryStatus.SUCCESS, refNo));

        EmailRecord latest = allEmailRecords.findLatestBy(toAddress, refNo);
        assertThat(latest.getDeliveryTime(), is(messageTime.plusDays(3)));
        assertThat(latest.getDeliveryStatus(), is(DeliveryStatus.SUCCESS));

        allEmailRecords.addOrReplace(new EmailRecord(fromAddress, toAddress, subject, message, messageTime.plusDays(6).plusMinutes(4), DeliveryStatus.SUCCESS, refNo2));
        allEmailRecords.addOrReplace(new EmailRecord(fromAddress, toAddress, subject, message, messageTime.plusHours(2), DeliveryStatus.PENDING, refNo2));

        latest = allEmailRecords.findLatestBy(toAddress, refNo2);
        assertThat(latest.getDeliveryTime(), is(messageTime.plusDays(6).plusMinutes(4)));
        assertThat(latest.getDeliveryStatus(), is(DeliveryStatus.SUCCESS));
    }

    @Test
    public void shouldCreateIdempotentMessages() {
        DeliveryStatus deliveryStatus = DeliveryStatus.PENDING;
        String refNo = "refNo";
        String fromAddress = "f@adr";
        String toAddress = "t@adr";
        String subject = "test-subject";
        String message = "test-message";
        DateTime messageTime = DateUtil.now().toDateTime(DateTimeZone.UTC);

        EmailRecord emailRecord = new EmailRecord(fromAddress, toAddress, subject, message, messageTime, deliveryStatus, refNo);
        allEmailRecords.addOrReplace(emailRecord);

        EmailRecord duplicateMessage = new EmailRecord(fromAddress, toAddress, subject, message, messageTime, deliveryStatus, refNo);
        allEmailRecords.addOrReplace(duplicateMessage);

        EmailRecords allMessages = allEmailRecords.findAllBy(new EmailRecordSearchCriteria().withReferenceNumber(refNo).withToAddress(toAddress));
        assertThat(allMessages.getRecords().size(), is(1));
    }

    @Test
    public void shouldUpdateTheDeliveryStatusForLatestRecordForMatchingRefNo() {
        String refNo = "refNo";
        String fromAddress = "f@adr";
        String toAddress = "t@adr";
        String subject = "test-subject";
        String message = "test-message";
        String message2 = "test-message-new";
        DateTime sentDate = DateUtil.now();

        final EmailRecord latestMessage = new EmailRecord(fromAddress, toAddress, subject, message2, sentDate, DeliveryStatus.PENDING, refNo);
        allEmailRecords.addOrReplace(latestMessage);
        allEmailRecords.addOrReplace(new EmailRecord(fromAddress, toAddress, subject, message, sentDate.minusMinutes(2), DeliveryStatus.PENDING, refNo));

        EmailRecord updatedEmail = allEmailRecords.get(latestMessage.getId());
        assertThat(updatedEmail.getMessage(), is(message2));
    }

    @After
    public void tearDown() {
        allEmailRecords.removeAll();
    }
}

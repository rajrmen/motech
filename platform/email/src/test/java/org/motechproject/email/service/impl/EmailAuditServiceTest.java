package org.motechproject.email.service.impl;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.email.DeliveryStatus;
import org.motechproject.email.domain.EmailRecord;
import org.motechproject.email.domain.EmailRecords;
import org.motechproject.email.repository.AllEmailRecords;
import org.motechproject.email.service.EmailAuditService;
import org.motechproject.email.service.EmailRecordSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testApplicationEmail.xml"})
public class EmailAuditServiceTest {

    @Autowired
    private EmailAuditService emailAuditService;

    @Autowired
    private AllEmailRecords allEmailRecords;

    @Test
    public void shouldRetrieveEmailAuditRecord() {
        EmailRecord emailRecord = createEmailRecord("to@address", DeliveryStatus.PENDING);
        emailAuditService.log(emailRecord);
        List<EmailRecord> emailRecords = emailAuditService.findAllEmailRecords();
        assertNotNull(emailRecords);
        assertTrue(emailRecords.size() > 0);
        assertEquals(emailRecords.get(0).getFromAddress(), emailRecord.getFromAddress());
        assertEquals(emailRecords.get(0).getToAddress(), emailRecord.getToAddress());
    }

    @Test
    public void shouldRetrieveEmailRecordWithSearchCriteria() {
        emailAuditService.log(createEmailRecord("to@address", DeliveryStatus.PENDING));
        emailAuditService.log(createEmailRecord("to@address2", DeliveryStatus.FAILURE));

        Set<DeliveryStatus> deliveryStatuses = new HashSet<>();
        deliveryStatuses.add(DeliveryStatus.PENDING);
        deliveryStatuses.add(DeliveryStatus.FAILURE);

        EmailRecordSearchCriteria criteriaDeliveryStatus = new EmailRecordSearchCriteria().withDeliveryStatuses(deliveryStatuses);
        EmailRecords emailRecordsDeliveryStatus = emailAuditService.findAllEmailRecords(criteriaDeliveryStatus);
        assertNotNull(emailRecordsDeliveryStatus.getRecords());
        assertThat(emailRecordsDeliveryStatus.getRecords().size(), is(2));

        EmailRecordSearchCriteria criteriaToAddress = new EmailRecordSearchCriteria().withToAddress("to@address");
        EmailRecords emailRecordsToAddress = emailAuditService.findAllEmailRecords(criteriaToAddress);
        assertNotNull(emailRecordsToAddress.getRecords());
        assertThat(emailRecordsToAddress.getRecords().size(), is(1));
    }

    private EmailRecord createEmailRecord(String toAddress, DeliveryStatus deliveryStatus) {
        return new EmailRecord("from@address", toAddress, "subject", "message", DateTime.now(), deliveryStatus, "1");
    }

    @After
    public void tearDown() {
        allEmailRecords.removeAll();
    }

}

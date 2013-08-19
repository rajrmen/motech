package org.motechproject.email.web;

import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.email.domain.DeliveryStatus;
import org.motechproject.email.domain.EmailRecord;
import org.motechproject.email.domain.EmailRecords;
import org.motechproject.email.service.EmailAuditService;
import org.motechproject.email.service.EmailSenderService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class EmailControllerTest {
    @Mock
    private EmailSenderService senderService;

    @Mock
    private EmailAuditService auditService;

    @InjectMocks
    private EmailController emailController = new EmailController();


    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldReturnRecordsFilteredByAddress() {
        when(auditService.findAllEmailRecords()).thenReturn(getTestEmailRecords());

        GridSettings filter = new GridSettings();
        filter.setSubject("gmail.com");
        EmailRecords recs = emailController.getEmails(filter);

        GridSettings filter2 = new GridSettings();
        filter2.setSubject("yahoo.com");
        EmailRecords recs2 = emailController.getEmails(filter2);

        assertNotNull(recs);
        assertThat(recs.getRecords(), is(4));
        assertNotNull(recs2);
        assertThat(recs2.getRecords(), is(3));
    }

    @Test
    public void shouldReturnRecordsFilteredByDate() {
        when(auditService.findAllEmailRecords()).thenReturn(getTestEmailRecords());

        GridSettings filter = new GridSettings();
        filter.setTimeFrom("1970-01-01 00:00:00");
        filter.setTimeTo("1970-01-01 00:00:03");
        EmailRecords recs = emailController.getEmails(filter);

        GridSettings filter2 = new GridSettings();
        filter2.setTimeFrom("1970-01-01 00:00:02");
        filter2.setTimeTo("1970-01-01 00:00:02");
        EmailRecords recs2 = emailController.getEmails(filter2);

        assertNotNull(recs);
        assertThat(recs.getRecords(), is(3));
        assertNotNull(recs2);
        assertThat(recs2.getRecords(), is(1));
    }

    @Test
    public void shouldSortByDate() {
        when(auditService.findAllEmailRecords()).thenReturn(getTestEmailRecords());

        GridSettings filter = new GridSettings();
        filter.setSortColumn("deliveryTime");
        filter.setSortDirection("asc");
        EmailRecords recs = emailController.getEmails(filter);

        assertNotNull(recs);
        assertThat(recs.getRows().get(0).getDeliveryTimeInDateTime(), is(DateUtil.setTimeZoneUTC(
                DateTimeFormat.forPattern("Y-MM-dd HH:mm:ss").parseDateTime("1970-01-01 00:00:01"))));
        assertThat(recs.getRows().get(1).getDeliveryTimeInDateTime(), is(DateUtil.setTimeZoneUTC(
                DateTimeFormat.forPattern("Y-MM-dd HH:mm:ss").parseDateTime("1970-01-01 00:00:02"))));
        assertThat(recs.getRows().get(2).getDeliveryTimeInDateTime(), is(DateUtil.setTimeZoneUTC(
                DateTimeFormat.forPattern("Y-MM-dd HH:mm:ss").parseDateTime("1970-01-01 00:00:03"))));
        assertThat(recs.getRows().get(3).getDeliveryTimeInDateTime(), is(DateUtil.setTimeZoneUTC(
                DateTimeFormat.forPattern("Y-MM-dd HH:mm:ss").parseDateTime("1970-01-01 00:00:04"))));
    }

    @Test
    public void shouldSortBySubject() {
        when(auditService.findAllEmailRecords()).thenReturn(getTestEmailRecords());

        GridSettings filter = new GridSettings();
        filter.setSortColumn("subject");
        filter.setSortDirection("asc");
        EmailRecords recs = emailController.getEmails(filter);

        assertNotNull(recs);
        assertThat(recs.getRows().get(0).getSubject(), is("Asubject3"));
        assertThat(recs.getRows().get(1).getSubject(), is("Bsubject5"));
        assertThat(recs.getRows().get(2).getSubject(), is("subject"));
    }

    @Test
    public void shouldReturnGivenRecord() {
        when(auditService.findAllEmailRecords()).thenReturn(getTestEmailRecords());

        GridSettings filter = new GridSettings();
        filter.setSortColumn("message");
        filter.setSortDirection("asc");
        emailController.getEmails(filter);
        EmailRecord rec1 = emailController.getEmail(1);
        EmailRecord rec4 = emailController.getEmail(4);

        assertNotNull(rec1);
        assertNotNull(rec4);
        assertThat(rec1.getMessage(), is("message"));
        assertThat(rec4.getMessage(), is("message4"));
    }

    @Test
    public void shouldReturnGivenRecordAfterFiltering() {
        when(auditService.findAllEmailRecords()).thenReturn(getTestEmailRecords());

        GridSettings filter = new GridSettings();
        filter.setSortColumn("message");
        filter.setSortDirection("asc");
        filter.setSubject("@gmail.com");
        emailController.getEmails(filter);
        EmailRecord rec1 = emailController.getEmail(1);
        EmailRecord rec3 = emailController.getEmail(3);

        assertNotNull(rec1);
        assertNotNull(rec3);
        assertThat(emailController.getEmails(filter).getRecords(), is(4));
        assertThat(rec1.getMessage(), is("message"));
        assertThat(rec3.getMessage(), is("message3"));
    }

    @Test
    public void shouldReturnGivenRecordAfterSorting() {
        when(auditService.findAllEmailRecords()).thenReturn(getTestEmailRecords());

        GridSettings filter = new GridSettings();
        filter.setSortColumn("deliveryTime");
        filter.setSortDirection("desc");
        emailController.getEmails(filter);
        EmailRecord rec1 = emailController.getEmail(1);
        EmailRecord rec4 = emailController.getEmail(4);

        assertNotNull(rec1);
        assertNotNull(rec4);
        assertThat(rec1.getMessage(), is("message2"));
        assertThat(rec4.getMessage(), is("message5"));
    }

    @Test
    public void shouldReturnProperMailsForAutoComplete() {
        when(auditService.findAllEmailRecords()).thenReturn(getTestEmailRecords());

        List<String> available = emailController.getAvailableMails("subject", "abc");
        List<String> available2 = emailController.getAvailableMails("subject", "def");
        List<String> available3 = emailController.getAvailableMails("subject", "abc");
        List<String> available4 = emailController.getAvailableMails("subject", "abc");
        List<String> available5 = emailController.getAvailableMails("subject", "abc@g");

        assertNotNull(available);
        assertNotNull(available2);
        assertNotNull(available3);
        assertNotNull(available4);
        assertNotNull(available5);
        assertThat(available.size(), is(2));
        assertThat(available2.size(), is(1));
        assertThat(available3.size(), is(1));
        assertThat(available4.size(), is(2));
        assertThat(available5.size(), is(1));
    }


    private List<EmailRecord> getTestEmailRecords() {
        List<EmailRecord> records = new ArrayList<>();
        records.add(new EmailRecord("abc@gmail.com", "def@gmail.com", "subject", "message",
                DateTimeFormat.forPattern("Y-MM-dd HH:mm:ss").parseDateTime("1970-01-01 00:00:01"), DeliveryStatus.PENDING));
        records.add(new EmailRecord("def@gmail.com", "abc@gmail.com", "subject2", "message2",
                DateTimeFormat.forPattern("Y-MM-dd HH:mm:ss").parseDateTime("1970-01-01 00:00:05"), DeliveryStatus.PENDING));
        records.add(new EmailRecord("abc@yahoo.com", "def@gmail.com", "Asubject3", "message3",
                DateTimeFormat.forPattern("Y-MM-dd HH:mm:ss").parseDateTime("1970-01-01 00:00:03"), DeliveryStatus.PENDING));
        records.add(new EmailRecord("abc@yahoo.com", "abc@gmail.com", "subject4", "message4",
                DateTimeFormat.forPattern("Y-MM-dd HH:mm:ss").parseDateTime("1970-01-01 00:00:04"), DeliveryStatus.PENDING));
        records.add(new EmailRecord("abc@yahoo.com", "def@yahoo.com", "Bsubject5", "message5",
                DateTimeFormat.forPattern("Y-MM-dd HH:mm:ss").parseDateTime("1970-01-01 00:00:02"), DeliveryStatus.PENDING));
        return records;
    }
}

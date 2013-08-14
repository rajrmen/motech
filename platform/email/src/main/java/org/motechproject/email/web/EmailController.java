package org.motechproject.email.web;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.email.domain.DeliveryStatus;
import org.motechproject.email.domain.EmailRecord;
import org.motechproject.email.domain.EmailRecordComparator;
import org.motechproject.email.domain.EmailRecords;
import org.motechproject.email.service.EmailAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * The <code>EmailController</code> class is used by view layer for getting information
 * about all {@Link EmailRecords} or single {@Link EmailRecord}. It stores the most recent
 * records and allows filtering and sorting them by given criteria.
 */

@Controller
public class EmailController {

    @Autowired
    private EmailAuditService auditService;

    private EmailRecords previousEmailRecords;

    @RequestMapping(value = "/emails", method= RequestMethod.GET)
    @ResponseBody
    public EmailRecords getEmails(GridSettings filter) {
        List<EmailRecord> records = auditService.findAllEmailRecords();
        List<EmailRecord> filtered = new ArrayList<>();

        Boolean sortAscending = true;
        DateTime dateFrom = getMinDateTime();
        DateTime dateTo = getMaxDateTime();

        if (filter.getSortDirection()!=null) {
            sortAscending = filter.getSortDirection().equals("asc");
        }

        if (filter.getTimeFrom() != null && (!filter.getTimeFrom().isEmpty())) {
            dateFrom = DateTimeFormat.forPattern("Y-MM-dd HH:mm:ss").parseDateTime(filter.getTimeFrom());
        }

        if (filter.getTimeTo() != null && (!filter.getTimeTo().isEmpty())) {
            dateTo = DateTimeFormat.forPattern("Y-MM-dd HH:mm:ss").parseDateTime(filter.getTimeTo());
        }

        filtered = filterByDates(records, dateFrom, dateTo);

        if (filter.getDeliveryStatus()!=null) {
            filtered = filterByStatus(filtered, filter.getDeliveryStatusFromSettings());
        }

        if (filter.getName()!=null) {
            filtered = filterByPartialString(filtered, filter.getName());
        }

        if (filter.getSortColumn()!=null && (!filtered.isEmpty())) {
            Collections.sort(
                    filtered, new EmailRecordComparator(sortAscending, filter.getSortColumn())
            );
        }

        previousEmailRecords = new EmailRecords(filter.getPage(), filter.getRows(), filtered);

        return previousEmailRecords;
    }

    @RequestMapping(value = "/emails/months/", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getAvailableMonths() {
        List<String> availableMonths = new ArrayList<>();

        List<EmailRecord> records = auditService.findAllEmailRecords();
        for (EmailRecord record : records) {
            String month = record.getDeliveryTimeInDateTime().monthOfYear().getAsText();
            String year = record.getDeliveryTimeInDateTime().year().getAsText();

            if (!availableMonths.contains(month+" "+year)) {
                availableMonths.add(month+" "+year);
            }
        }

        return availableMonths;
    }

    @RequestMapping(value = "/emails/available/", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getAvailableMails(@RequestParam("autoComplete") String autoComplete,
                                          @RequestParam("term") String partialAddress) {

        List<String> availableAddress = new ArrayList<>();

        if (autoComplete.equals("fromAddress")) {
            availableAddress = getAllFromAddressContaining(partialAddress);
        } else if (autoComplete.equals("toAddress")) {
            availableAddress = getAllToAddressContaining(partialAddress);
        } else if (autoComplete.equals("fromToAddress")) {
            availableAddress = getAllFromAddressContaining(partialAddress);
            List<String> availableAddress2 = getAllToAddressContaining(partialAddress);

            for (String address : availableAddress2) {
                if (!availableAddress.contains(address)) {
                    availableAddress.add(address);
                }
            }
        }

        return availableAddress;
    }

    @RequestMapping({ "/emails/{mailid}" })
    @ResponseBody
    public EmailRecord getEmail(@PathVariable int mailid) {
        EmailRecord record = null;
        if (previousEmailRecords != null) {
            record = previousEmailRecords.getRows().get(mailid-1);
        }
        return record;
    }


    private List<EmailRecord> filterByDates(List<EmailRecord> records, DateTime from, DateTime to) {
        List<EmailRecord> filtered = new ArrayList<>();

        for (EmailRecord record : records) {
            if ((record.getDeliveryTimeInDateTime().isAfter(from) &&
                    record.getDeliveryTimeInDateTime().isBefore(to)) ||
                    record.getDeliveryTimeInDateTime().isEqual(from) ||
                    record.getDeliveryTimeInDateTime().isEqual(to)) {
                filtered.add(record);
            }
        }

        return filtered;
    }

    private List<EmailRecord> filterByStatus(List<EmailRecord> records, Set<DeliveryStatus> status) {
        List<EmailRecord> filtered = new ArrayList<>();

        for (EmailRecord record : records) {
            if (status.contains(record.getDeliveryStatus())) {
                filtered.add(record);
            }
        }

        return filtered;
    }

    private List<EmailRecord> filterByPartialString(List<EmailRecord> records, String partial) {
        List<EmailRecord> filtered = new ArrayList<>();

        for (EmailRecord record : records) {
            if (record.getFromAddress().contains(partial) || record.getToAddress().contains(partial)
                    || record.getSubject().contains(partial)) {
                filtered.add(record);
            }
        }

        return filtered;
    }

    private List<String> getAllFromAddressContaining(String partial) {
        List<String> available = new ArrayList<>();

        List <EmailRecord> records = auditService.findAllEmailRecords();
        for (EmailRecord record : records) {
            if (record.getFromAddress().contains(partial) && (!available.contains(record.getFromAddress()))) {
                available.add(record.getFromAddress());
            }
        }

        return available;
    }

    private List<String> getAllToAddressContaining(String partial) {
        List<String> available = new ArrayList<>();

        List <EmailRecord> records = auditService.findAllEmailRecords();
        for (EmailRecord record : records) {
            if (record.getToAddress().contains(partial) && (!available.contains(record.getToAddress()))) {
                available.add(record.getToAddress());
            }
        }

        return available;
    }

    private DateTime getMinDateTime() {
        return new DateTime(Long.MIN_VALUE);
    }

    private DateTime getMaxDateTime() {
        return new DateTime(Long.MAX_VALUE);
    }
}

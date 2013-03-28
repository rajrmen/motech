package org.motechproject.sms.api.web;

import org.motechproject.sms.api.domain.SmsRecord;

import java.util.List;

public class SmsRecords {

    private int count;
    private List<SmsRecord> records;

    public SmsRecords(int count, List<SmsRecord> records) {
        this.count = count;
        this.records = records;
    }

    public int getCount() {
        return count;
    }

    public List<SmsRecord> getRecords() {
        return records;
    }

}

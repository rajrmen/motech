package org.motechproject.email.domain;

import java.util.ArrayList;
import java.util.List;

public class EmailRecords {
    private int count;
    private List<EmailRecord> records;

    public EmailRecords() {
        this.count = 0;
        this.records = new ArrayList<>();
    }

    public EmailRecords(int count, List<EmailRecord> records) {
        this.count = count;
        this.records = records;
    }

    public int getCount() {
        return count;
    }

    public List<EmailRecord> getRecords() {
        return records;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setRecords(List<EmailRecord> records) {
        this.records = records;
    }
}

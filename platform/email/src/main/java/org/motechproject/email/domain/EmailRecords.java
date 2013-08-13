package org.motechproject.email.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * The <code>EmailRecords</code> class wraps the {@Link EmailRecord} list for view layer and
 * stores current item count.
 */

public class EmailRecords {
    private Integer records;
    private List<EmailRecord> rows;

    public EmailRecords() {
        this.records = 0;
        this.rows = new ArrayList<>();
    }

    public EmailRecords(int records, List<EmailRecord> rows) {
        this.records = records;
        this.rows = rows;
    }

    public Integer getRecords() {
        return records;
    }

    public List<EmailRecord> getRows() {
        return rows;
    }

    public void setCount(int records) {
        this.records = records;
    }

    public void setRows(List<EmailRecord> rows) {
        this.rows = rows;
        this.records = rows.size();
    }
}
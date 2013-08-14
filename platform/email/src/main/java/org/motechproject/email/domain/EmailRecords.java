package org.motechproject.email.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * The <code>EmailRecords</code> class wraps the {@Link EmailRecord} list for view layer and
 * stores current item count.
 */

public class EmailRecords {
    private Integer records;
    private Integer total;
    private Integer page;
    private List<EmailRecord> rows;

    public EmailRecords() {
        this.records = 0;
        this.rows = new ArrayList<>();
    }

    public EmailRecords(Integer page, Integer rows, List<EmailRecord> allRecords) {
        this.page = page;
        this.records = allRecords.size();
        this.total = (this.records <= rows) ? 1 : (this.records / rows) + 1;

        this.rows = new ArrayList<>(allRecords.subList((page-1)*rows, (page*rows>this.records ? this.records : page*rows)));
    }

    public Integer getTotal() {
        return total;
    }

    public Integer getPage() {
        return page;
    }

    public Integer getRecords() {
        return records;
    }

    public List<EmailRecord> getRows() {
        return rows;
    }

    public void setRows(List<EmailRecord> rows) {
        this.rows = rows;
        this.records = rows.size();
    }

    @Override
    public String toString() {
        return String.format("EmailRecords{page=%d, total=%d, records=%d, rows=%s}", page, total, records, rows);
    }
}
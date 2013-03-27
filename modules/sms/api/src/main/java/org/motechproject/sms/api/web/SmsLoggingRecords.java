package org.motechproject.sms.api.web;

import org.motechproject.sms.api.domain.SmsRecord;

import java.io.Serializable;
import java.util.List;

public class SmsLoggingRecords implements Serializable {

    private static final long serialVersionUID = -6205245415683301270L;

    private final Integer page;
    private final Integer total;
    private final Integer records;
    private final List<SmsRecord> rows;

    public SmsLoggingRecords(Integer page, Integer rows, List<SmsRecord> list) {
        this.page = page;
        records = list.size();
        total = records <= rows ? 1 : (records / rows) + 1;

        Integer start = rows * (page > total ? total : page) - rows;
        Integer count = start + rows;
        Integer end = count > records ? records : count;


        this.rows = list.subList(start, end);
    }

    public Integer getPage() {
        return page;
    }

    public Integer getTotal() {
        return total;
    }

    public Integer getRecords() {
        return records;
    }

    public List<SmsRecord> getRows() {
        return rows;
    }

    @Override
    public String toString() {
        return String.format("SmsLoggingRecords{page=%d, total=%d, records=%d, rows=%s}", page, total, records, rows);
    }
}

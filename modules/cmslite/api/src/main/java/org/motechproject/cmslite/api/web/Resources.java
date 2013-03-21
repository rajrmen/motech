package org.motechproject.cmslite.api.web;

import java.io.Serializable;
import java.util.List;

public class Resources implements Serializable {
    private static final long serialVersionUID = -6205245415683301270L;

    private final Integer page;
    private final Integer total;
    private final Integer records;
    private final List<ResourceDto> rows;

    public Resources(Integer page, Integer total, Integer records, List<ResourceDto> rows) {
        this.page = page;
        this.total = total;
        this.records = records;
        this.rows = rows;
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

    public List<ResourceDto> getRows() {
        return rows;
    }

    @Override
    public String toString() {
        return String.format("Resources{page=%d, total=%d, records=%d, rows=%s}", page, total, records, rows);
    }
}

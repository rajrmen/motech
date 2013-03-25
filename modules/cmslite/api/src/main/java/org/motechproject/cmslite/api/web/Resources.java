package org.motechproject.cmslite.api.web;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class Resources implements Serializable {
    private static final long serialVersionUID = -6205245415683301270L;

    private final Integer page;
    private final Integer total;
    private final Integer records;
    private final List<ResourceDto> rows;

    public Resources(Integer rows, Integer page, String sortColumn, String sortDirection, List<ResourceDto> list) {
        this.page = page;
        records = list.size();
        total = records <= rows ? 1 : (records / rows) + 1;

        Integer start = rows * (page > total ? total : page) - rows;
        Integer count = start + rows;
        Integer end = count > records ? records : count;

        Collections.sort(list, new ResourceComparator(sortColumn, sortDirection));
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

    public List<ResourceDto> getRows() {
        return rows;
    }

    @Override
    public String toString() {
        return String.format("Resources{page=%d, total=%d, records=%d, rows=%s}", page, total, records, rows);
    }
}

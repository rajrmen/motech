package org.motechproject.email.web;

import org.motechproject.email.domain.DeliveryStatus;

import java.util.HashSet;
import java.util.Set;

/**
 * The <code>GridSettings</code> class provides an information about
 * available filtering and sorting options for control layer
 */

public class GridSettings {

    private String name;
    private String deliveryStatus;
    private String from;
    private String to;
    private String sortColumn;
    private String sortDirection;

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    public String getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(String sortColumn) {
        this.sortColumn = sortColumn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return deliveryStatus;
    }

    public void setStatus(String status) {
        this.deliveryStatus = status;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Set<DeliveryStatus> getDeliveryStatusFromSettings() {
        Set<DeliveryStatus> statusList = new HashSet<>();
        String[] statuses = deliveryStatus.split(",");
        for (String status : statuses) {
            if (!status.isEmpty()) {
                statusList.add(DeliveryStatus.valueOf(status));
            }
        }
        return statusList;
    }
}

package org.motechproject.cmslite.api.web;

import java.util.Comparator;

public class ResourceComparator implements Comparator<ResourceDto> {
    private final String field;

    public ResourceComparator(String field) {
        this.field = field;
    }

    @Override
    public int compare(ResourceDto o1, ResourceDto o2) {
        int compare;

        switch (field) {
            case "name":
                compare = o1.getName().compareToIgnoreCase(o2.getName());
                break;
            case "type":
                compare = o1.getType().compareToIgnoreCase(o2.getType());
                break;
            default:
                compare = 0;
        }

        return compare;
    }
}

package org.motechproject.diagnostics.response;

import java.util.Comparator;

public class DiagnosticsResponseComparator implements Comparator<DiagnosticsResponse> {

    @Override
    public int compare(DiagnosticsResponse response1, DiagnosticsResponse response2) {
        String name1 = response1 == null ? null : response1.getName();
        String name2 = response2 == null ? null : response2.getName();

        return compareName(name1, name2);
    }

    private int compareName(String name1, String name2) {
        if(name1 == null && name2 == null) {
            return 0;
        }

        if(name2 == null) {
            return 1;
        }

        if(name1 == null) {
            return -1;
        }

        return name1.compareTo(name2);
    }
}

package org.motechproject.scheduler.domain;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import java.util.Comparator;

public class JobBasicInfoComparator implements Comparator<JobBasicInfo> {
    private String compareField = "activity";
    private Boolean ascending = true;

    public JobBasicInfoComparator(Boolean ascending, String compareField) {
        this.compareField = compareField;
        this.ascending = ascending;
    }

    @Override
    public int compare(JobBasicInfo o1, JobBasicInfo o2) {
        DateTime o1Time;
        DateTime o2Time;
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("Y-MM-dd HH:mm:ss");
        int ret = 0;

        switch (compareField) {
            case "status":
                ret =  o1.getStatus().compareTo(o2.getStatus());
                break;
            case "name":
                ret =  o1.getName().compareTo(o2.getName());
                break;
            case "startDate":
                o1Time = dateTimeFormatter.parseDateTime(o1.getStartDate());
                o2Time = dateTimeFormatter.parseDateTime(o2.getStartDate());

                ret = o1Time.compareTo(o2Time);
                break;
            case "nextFireDate":
                o1Time = dateTimeFormatter.parseDateTime(o1.getNextFireDate());
                o2Time = dateTimeFormatter.parseDateTime(o2.getNextFireDate());

                ret = o1Time.compareTo(o2Time);
                break;
            case "endDate":
                o1Time = dateTimeFormatter.parseDateTime(o1.getEndDate());
                o2Time = dateTimeFormatter.parseDateTime(o2.getEndDate());

                ret = o1Time.compareTo(o2Time);
                break;
            case "activity":
            default:
                ret =  o1.getActivity().compareTo(o2.getActivity());
                break;
        }

        return (ascending) ? ret : -ret;
    }
}

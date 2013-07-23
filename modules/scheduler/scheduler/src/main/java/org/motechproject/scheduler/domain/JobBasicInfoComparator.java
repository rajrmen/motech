package org.motechproject.scheduler.domain;

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
        int ret = 0;

        switch (compareField) {
            case "status":
                ret =  o1.getStatus().compareTo(o2.getStatus());
                break;
            case "name":
                ret =  o1.getName().compareTo(o2.getName());
                break;
            case "startdate":
                ret =  o1.getStartDate().compareTo(o2.getStartDate());
                break;
            case "nextfiredate":
                ret =  o1.getNextFireDate().compareTo(o2.getNextFireDate());
                break;
            case "enddate":
                ret =  o1.getEndDate().compareTo(o2.getEndDate());
                break;
            case "activity":
            default:
                ret =  o1.getActivity().compareTo(o2.getActivity());
                break;
        }

        return (ascending) ? ret : -ret;
    }
}

package org.motechproject.admin.web.controller;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.admin.domain.JobsRecords;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.JobBasicInfo;
import org.motechproject.scheduler.domain.JobDetailedInfo;
import org.motechproject.scheduler.domain.JobBasicInfoComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;


@Controller
public class JobsController {
    @Autowired
    private MotechSchedulerService motechSchedulerService;

    private JobsRecords previusJobsRecords;

    @RequestMapping({ "/jobs" })
    @ResponseBody
    public JobsRecords handleJobs(JobsGridSettings jobsGridSettings) {
        List<JobBasicInfo> allJobsBasicInfos = motechSchedulerService.getScheduledJobsBasicInfo();
        List<JobBasicInfo> filteredJobsBasicInfos = null;
        Boolean sortAscending = (jobsGridSettings.getSortDirection().equals("asc"));
        DateTime dateFrom;
        DateTime dateTo;


        if (!jobsGridSettings.getTimeFrom().isEmpty()) {
            dateFrom = DateTimeFormat.forPattern("Y-MM-dd HH:mm:ss")
                    .parseDateTime(jobsGridSettings.getTimeFrom());
        } else {
            dateFrom = getMinDateTime();
        }

        if (!jobsGridSettings.getTimeTo().isEmpty()) {
            dateTo = DateTimeFormat.forPattern("Y-MM-dd HH:mm:ss")
                    .parseDateTime(jobsGridSettings.getTimeTo());
        } else {
            dateTo = getMaxDateTime();
        }

        filteredJobsBasicInfos = filterJobsByDates(allJobsBasicInfos, dateFrom, dateTo);

        filteredJobsBasicInfos = filterJobsByStates(
                filteredJobsBasicInfos, jobsGridSettings.getActivity(), jobsGridSettings.getStatus()
        );

        filteredJobsBasicInfos = filterJobsByName(filteredJobsBasicInfos, jobsGridSettings.getName());

        if (!jobsGridSettings.getSortColumn().isEmpty()) {
            Collections.sort(
                    filteredJobsBasicInfos, new JobBasicInfoComparator(
                        sortAscending,
                        jobsGridSettings.getSortColumn()
                    )
            );
        }

        previusJobsRecords = new JobsRecords(
            jobsGridSettings.getPage(), jobsGridSettings.getRows(), filteredJobsBasicInfos
        );

        return previusJobsRecords;
    }

    @RequestMapping({ "/jobs/{jobid}" })
    @ResponseBody
    public JobDetailedInfo handleJob(@PathVariable int jobid) {
        if (previusJobsRecords != null) {
            return motechSchedulerService.getScheduledJobDetailedInfo(previusJobsRecords.getRows().get(jobid-1));
        } else {
            return null;
        }
    }

    private List<JobBasicInfo> filterJobsByDates(List<JobBasicInfo> jobs, DateTime dateFrom, DateTime dateTo) {
        List<JobBasicInfo> filteredJobs = new ArrayList<>();

        for (JobBasicInfo job : jobs) {
            DateTime jobStartTime = DateTimeFormat.forPattern("Y-MM-dd HH:mm:ss")
                    .parseDateTime(job.getStartDate());

            if (jobStartTime.isAfter(dateFrom) && jobStartTime.isBefore(dateTo)) {
                filteredJobs.add(job);
            }
        }

        return filteredJobs;
    }

    private List<JobBasicInfo> filterJobsByStates(List<JobBasicInfo> jobs, String activityFilter, String statusFilter) {
        List<JobBasicInfo> filteredJobs = new ArrayList<>();

        for (JobBasicInfo job : jobs) {
            if (activityFilter.contains(job.getActivity()) && statusFilter.contains(job.getStatus())) {
                filteredJobs.add(job);
            }
        }

        return filteredJobs;
    }

    private List<JobBasicInfo> filterJobsByName(List<JobBasicInfo> jobs, String namePartial) {
        List<JobBasicInfo> filteredJobs = new ArrayList<>();

        for (JobBasicInfo job : jobs) {
            if (job.getName().contains(namePartial)) {
                filteredJobs.add(job);
            }
        }

        return filteredJobs;
    }

    private DateTime getMinDateTime() {
        return new DateTime(Long.MIN_VALUE);
    }

    private DateTime getMaxDateTime() {
        return new DateTime(Long.MAX_VALUE);
    }
}

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

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;


@Controller
public class JobsController {
    @Autowired
    private MotechSchedulerService motechSchedulerService;

    @RequestMapping({ "/jobs" })
    @ResponseBody
    public JobsRecords handleJobs(JobsGridSettings jobsGridSettings) {
        List<JobBasicInfo> allJobsBasicInfos = motechSchedulerService.getScheduledJobsBasicInfo();
        List<JobBasicInfo> filteredJobsBasicInfos = new ArrayList<>();
        Boolean sortAscending = (jobsGridSettings.getSortDirection().equals("asc"));
        DateTime dateFrom = new DateTime();
        DateTime dateTo = new DateTime();

        if (!jobsGridSettings.getDateFrom().isEmpty()) {
            dateFrom = DateTimeFormat.forPattern("Y-MM-dd")
                    .parseDateTime(jobsGridSettings.getDateFrom());
        }

        if (!jobsGridSettings.getDateTo().isEmpty()) {
            dateTo = DateTimeFormat.forPattern("Y-MM-dd")
                    .parseDateTime(jobsGridSettings.getDateTo());
        }

        for (JobBasicInfo job : allJobsBasicInfos) {
            DateTime jobStartTime = DateTimeFormat.forPattern("Y-MM-dd hh:mm:ss")
                    .parseDateTime(job.getStartDate());
            DateTime jobEndTime = DateTimeFormat.forPattern("Y-MM-dd hh:mm:ss")
                    .parseDateTime(job.getEndDate());
            int ifAddJob = 0;

            if (    jobsGridSettings.getActivity().contains(job.getActivity()) &&
                    jobsGridSettings.getStatus().contains(job.getStatus()) &&
                    job.getName().toLowerCase().contains(jobsGridSettings.getName().toLowerCase()) )
            {
                ifAddJob = 1;

                if (!jobsGridSettings.getDateFrom().isEmpty()) {
                    if (dateFrom.isBefore(jobStartTime) || dateFrom.isBefore(jobEndTime)) {
                        ifAddJob += 1;
                    } else {
                        ifAddJob -= 1;
                    }
                }

                if (!jobsGridSettings.getDateTo().isEmpty()) {
                    if (dateTo.isAfter(jobStartTime) || dateFrom.isAfter(jobEndTime)) {
                        ifAddJob += 1;
                    } else {
                        ifAddJob -= 1;
                    }
                }

                if (ifAddJob > 0) {
                    filteredJobsBasicInfos.add(job);
                }
            }
        }

        if (!jobsGridSettings.getSortColumn().isEmpty()) {
            Collections.sort(
                    filteredJobsBasicInfos, new JobBasicInfoComparator(
                        sortAscending,
                        jobsGridSettings.getSortColumn()
                    )
            );
        }

        return new JobsRecords(
                jobsGridSettings.getPage(), jobsGridSettings.getRows(), filteredJobsBasicInfos
        );
    }

    @RequestMapping({ "/jobs/{jobname}" })
    @ResponseBody
    public JobDetailedInfo handleJob(@PathVariable String jobname) {
        return motechSchedulerService.getScheduledJobDetailedInfo(jobname);
    }
}

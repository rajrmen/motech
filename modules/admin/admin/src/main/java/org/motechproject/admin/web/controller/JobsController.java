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
        List<JobBasicInfo> filteredJobsBasicInfos = new ArrayList<>();
        Boolean sortAscending = (jobsGridSettings.getSortDirection().equals("asc"));
        DateTime dateFrom = new DateTime();
        DateTime dateTo = new DateTime();

        if (!jobsGridSettings.getTimeFrom().isEmpty()) {
            dateFrom = DateTimeFormat.forPattern("Y-MM-dd hh:mm:ss")
                    .parseDateTime(jobsGridSettings.getTimeFrom());
        }

        if (!jobsGridSettings.getTimeTo().isEmpty()) {
            dateTo = DateTimeFormat.forPattern("Y-MM-dd hh:mm:ss")
                    .parseDateTime(jobsGridSettings.getTimeTo());
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

                if (!jobsGridSettings.getTimeFrom().isEmpty()) {
                    if (dateFrom.isBefore(jobStartTime) || dateFrom.isBefore(jobEndTime)) {
                        ifAddJob += 1;
                    } else {
                        ifAddJob -= 1;
                    }
                }

                if (!jobsGridSettings.getTimeTo().isEmpty()) {
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
}

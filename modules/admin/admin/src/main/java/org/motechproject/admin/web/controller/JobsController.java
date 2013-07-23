package org.motechproject.admin.web.controller;

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

        for (JobBasicInfo job : allJobsBasicInfos) {
            if (jobsGridSettings.getActivity().contains(job.getActivity()) &&
                    jobsGridSettings.getStatus().contains(job.getStatus()) )
            {
                filteredJobsBasicInfos.add(job);
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

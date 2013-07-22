package org.motechproject.admin.web.controller;

import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.JobBasicInfo;
import org.motechproject.scheduler.domain.JobDetailedInfo;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class JobsController {
    @Autowired
    private MotechSchedulerService motechSchedulerService;

    @RequestMapping({ "/jobs" })
    @ResponseBody
    public List<JobBasicInfo> handleJobs() {
        return motechSchedulerService.getScheduledJobsBasicInfo();
    }

    @RequestMapping({ "/jobs/{jobid}" })
    @ResponseBody
    public JobDetailedInfo handleJob(@PathVariable int jobid) {
        return motechSchedulerService.getScheduledJobDetailedInfo(
                motechSchedulerService.getScheduledJobsBasicInfo().get(jobid)
        );
    }
}

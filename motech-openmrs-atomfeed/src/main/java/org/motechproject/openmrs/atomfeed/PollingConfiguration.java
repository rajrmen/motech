package org.motechproject.openmrs.atomfeed;

import java.util.Date;

import org.joda.time.LocalTime;
import org.motechproject.MotechException;
import org.motechproject.openmrs.atomfeed.events.EventSubjects;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduler.domain.RepeatingSchedulableJob;

public class PollingConfiguration {

    private static final Long MILLISECONDS_IN_DAY = 1000 * 60 * 24L;
    private final MotechSchedulerService scheduleService;
    private boolean daily;
    private int hour;
    private int minute;

    public PollingConfiguration(MotechSchedulerService scheduleService, String string, String time) {
        if (!"daily".equals(string)) {
            throw new MotechException("Motech OpenMRS Atom Feed polling configuration can only be: interval or daily");
        }
        
        if ("daily".equals(string)) {
            daily = true;
            String[] hourMinutes = time.split(":");
            if (hourMinutes.length != 2) {
                // exception
            }
            
            hour = Integer.parseInt(hourMinutes[0]);
            minute = Integer.parseInt(hourMinutes[1]);
        }
        
        this.scheduleService = scheduleService;
    }

    public void schedulePolling() {
        if (daily) {
            MotechEvent event = new MotechEvent(EventSubjects.POLLING_SUBJECT);
            LocalTime time = new LocalTime(hour, minute);
            Date realTime = time.toDateTimeToday().toDate();
            RepeatingSchedulableJob job = new RepeatingSchedulableJob(event, realTime, null, MILLISECONDS_IN_DAY);
            scheduleService.safeScheduleRepeatingJob(job);
        }
    }

}

package org.motechproject.openmrs.atomfeed;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.MotechException;
import org.motechproject.openmrs.atomfeed.events.EventSubjects;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduler.domain.RepeatingSchedulableJob;

public class PollingConfigurationTest {

    private static final Long MILLISECONDS_IN_DAY = 1000 * 60 * 24L;
    @Mock
    MotechSchedulerService scheduleService;
    
    @Before
    public void setUp() {
        initMocks(this);
    }
    
    @Test(expected = MotechException.class)
    public void showThrowExceptionOnBadPollingTypeConfiguration() {
        new PollingConfiguration(null, "bad", null);
    }

    @Test
    public void shouldConfigureDailyPolling() {
        PollingConfiguration pollConfig = new PollingConfiguration(scheduleService, "daily", "10:00");
        pollConfig.schedulePolling();
        
        RepeatingSchedulableJob expected = createExceptedRepeatSchedulableJob();
        ArgumentCaptor<RepeatingSchedulableJob> repeatingJob = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(scheduleService).safeScheduleRepeatingJob(repeatingJob.capture());
        
        assertEquals(expected, repeatingJob.getValue());
    }

    private RepeatingSchedulableJob createExceptedRepeatSchedulableJob() {
        MotechEvent event = new MotechEvent(EventSubjects.POLLING_SUBJECT);
        LocalTime startTime = new LocalTime(10, 0);
        RepeatingSchedulableJob job = new RepeatingSchedulableJob(event, startTime.toDateTimeToday().toDate(), null, MILLISECONDS_IN_DAY);
        return job;
    }
}

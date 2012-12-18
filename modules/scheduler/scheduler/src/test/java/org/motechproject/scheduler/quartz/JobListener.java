package org.motechproject.scheduler.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JobListener implements Job {

    private List<Date> fireTimes;
    private Object lock;

    public JobListener() {
        fireTimes = new ArrayList<>();
        lock = new Object();
    }

    private static class SingletonHolder {
        public static final JobListener INSTANCE = new JobListener();
    }

    public static JobListener getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public Object getLock() {
        return lock;
    }

    public List<Date> getFireTimes() {
        return fireTimes;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        fireTimes.add(new Date());
        if (context.getNextFireTime() == null) {
            synchronized (lock) {
                lock.notifyAll();
            }
        }
    }
}

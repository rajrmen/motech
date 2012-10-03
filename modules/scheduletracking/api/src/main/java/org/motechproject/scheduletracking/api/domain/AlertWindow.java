package org.motechproject.scheduletracking.api.domain;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.motechproject.model.Time;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.filter;
import static org.motechproject.util.DateUtil.newDateTime;
import static org.motechproject.util.DateUtil.now;

public class AlertWindow {
    private DateTime enrolledOn;
    private Alert alert;
    private DateTime alertWindowStart;
    private DateTime alertWindowEnd;
    private List<DateTime> schedulableAlertTimings;
    private Time preferredAlertTime;
    private final List<DateTime> allAlertTimings;

    public AlertWindow(DateTime windowStart, DateTime windowEnd, DateTime enrolledOn, Time preferredAlertTime, Alert alert) {
        this.alertWindowStart = toPreferredTime(windowStart.plus(alert.getOffset()), preferredAlertTime);
        this.alertWindowEnd = windowEnd;
        this.preferredAlertTime = preferredAlertTime;
        this.enrolledOn = enrolledOn;
        this.alert = alert;

        allAlertTimings = computeAllAlertTimings();
        schedulableAlertTimings = alertsFallingInAlertWindow(allAlertTimings);
    }

    public int numberOfAlertsToSchedule() {
        return schedulableAlertTimings.size();
    }

    public DateTime scheduledAlertStartDate() {
        if (schedulableAlertTimings.size() == 0) {
            return null;
        }
        return schedulableAlertTimings.get(0);
    }

    public List<DateTime> allPossibleAlerts() {
        return allAlertTimings;

    }

    private List<DateTime> computeAllAlertTimings() {
        List<DateTime> alertTimings = new ArrayList<>();

        if (alert.getCount() > 0) {
            alertTimings.add(alertWindowStart);
        }
        for (int alertIndex = 1; alertIndex < alert.getCount(); alertIndex++) {
            DateTime previousAlertTime = alertTimings.get(alertIndex - 1);
            alertTimings.add(previousAlertTime.plus(alert.getInterval()));
        }

        boolean isDelayed = earliestValidAlertDateTime().isAfter(alertWindowStart);
        if (alert.isFloating() && isDelayed) {
            return floatWindowAndAlertTimings(alertTimings);
        }

        return alertTimings;
    }

    private List<DateTime> floatWindowAndAlertTimings(List<DateTime> alertTimings) {
        List<DateTime> floatedAlertTimings = new ArrayList<>();

        DateTime floatingAlertsStartDateTime = newDateTime(earliestValidAlertDateTime().toLocalDate(), new Time(alertWindowStart.getHourOfDay(), alertWindowStart.getMinuteOfHour()));

        Period periodToBeFloatedWith = new Period(alertWindowStart, floatingAlertsStartDateTime);
        alertWindowStart = alertWindowStart.plus(periodToBeFloatedWith);

        // Schedule floating alerts from tomorrow, if today's alert time has already passed
        if (preferredAlertTime != null && alertWindowStart.isBefore(DateUtil.now())) {
            periodToBeFloatedWith = periodToBeFloatedWith.plusDays(1);
        }

        for (DateTime alertTime : alertTimings) {
            floatedAlertTimings.add(alertTime.plus(periodToBeFloatedWith));
        }

        return floatedAlertTimings;
    }

    private List<DateTime> alertsFallingInAlertWindow(List<DateTime> alertTimings) {
        List<DateTime> alertsWithInEndDate = filterAlertsBeyondEndDate(alertTimings);
        return filterElapsedAlerts(alertsWithInEndDate);
    }

    private List<DateTime> filterElapsedAlerts(List<DateTime> alertsWithInEndDate) {
        DateTime start = earliestValidAlertDateTime();

        // In case of floating alerts with no preferred alert time, don't filter out today's alerts.
        if (alert.isFloating() && preferredAlertTime == null) {
            start = newDateTime(start.toLocalDate(), new Time(0, 0));
        }

        return filter(greaterThanOrEqualTo(start), alertsWithInEndDate);
    }

    private List<DateTime> filterAlertsBeyondEndDate(List<DateTime> alertTimings) {
        return filter(lessThan(alertWindowEnd), alertTimings);
    }

    private DateTime toPreferredTime(DateTime alertTime, Time preferredTime) {
        if (preferredTime == null) {
            return alertTime;
        }
        return newDateTime(alertTime.toLocalDate(), preferredTime.getHour(), preferredTime.getMinute(), 0);
    }

    private DateTime earliestValidAlertDateTime() {
        DateTime now = now();
        return !enrolledOn.isAfter(now) ? now : enrolledOn;
    }
    private static Matcher<?> greaterThanOrEqualTo(final DateTime dateTime) {
        return new BaseMatcher<DateTime>() {
            @Override
            public boolean matches(Object o) {
                return !((DateTime) o).isBefore(dateTime);
            }

            @Override
            public void describeTo(Description description) {
            }
        };
    }

    private static Matcher<?> lessThan(final DateTime dateTime) {
        return new BaseMatcher<DateTime>() {
            @Override
            public boolean matches(Object o) {
                return ((DateTime) o).isBefore(dateTime);
            }

            @Override
            public void describeTo(Description description) {
            }
        };
    }
}

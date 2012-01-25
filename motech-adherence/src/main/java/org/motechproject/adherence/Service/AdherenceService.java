package org.motechproject.adherence.Service;

import org.joda.time.LocalDate;
import org.motechproject.adherence.dao.AllAdherenceLogs;
import org.motechproject.adherence.domain.AdherenceLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdherenceService {

    private AllAdherenceLogs allAdherenceLogs;

    @Autowired
    public AdherenceService(AllAdherenceLogs allAdherenceLogs) {
        this.allAdherenceLogs = allAdherenceLogs;
    }

    public void recordDoseTaken(String externalId, boolean taken) {
        AdherenceLog latestLog = allAdherenceLogs.findLatestLog(externalId);
        int dosesTaken = taken ? 1 : 0;
        if (latestLog == null) {
            AdherenceLog newLog = AdherenceLog.create(externalId).addAdherence(dosesTaken, 1);
            allAdherenceLogs.add(newLog);
        } else {
            AdherenceLog newLog = latestLog.addAdherence(dosesTaken, 1);
            allAdherenceLogs.add(newLog);
        }
    }

    public void recordAdherence(String externalId, int taken, int totalDoses, LocalDate fromDate, LocalDate toDate) {
        AdherenceLog latestLog = allAdherenceLogs.findLatestLog(externalId);
        if (latestLog == null) {
            AdherenceLog newLog = AdherenceLog.create(externalId).addAdherence(taken, totalDoses);
            newLog.setFromDate(fromDate);
            newLog.setToDate(toDate);
            allAdherenceLogs.add(newLog);
        } else {
            AdherenceLog newLog = latestLog.addAdherence(taken, totalDoses);
            newLog.setFromDate(fromDate);
            newLog.setToDate(toDate);
            allAdherenceLogs.add(newLog);
        }
    }

    public double getRunningAverageAdherence(String externalId) {
        AdherenceLog latestLog = allAdherenceLogs.findLatestLog(externalId);
        if (latestLog == null) {
            return 0;
        } else {
            return ((double) latestLog.getDosesTaken()) / latestLog.getTotalDoses();
        }
    }

    public double getDeltaAdherence(String externalId) {
        AdherenceLog latestLog = allAdherenceLogs.findLatestLog(externalId);
        if (latestLog == null) {
            return 0;
        } else {
            return ((double) latestLog.getDeltaDosesTaken()) / latestLog.getDeltaTotalDoses();
        }
    }

    public double getDeltaAdherence(String externalId, LocalDate fromDate, LocalDate toDate) {
        List<AdherenceLog> logs = allAdherenceLogs.findLogsBetween(externalId, fromDate, toDate);
        double dosesTaken = 0;
        double totalDoses = 0;
        for (AdherenceLog adherenceLog : logs) {
            dosesTaken += adherenceLog.getDeltaDosesTaken();
            totalDoses += adherenceLog.getDeltaTotalDoses();
        }
        return totalDoses == 0 ? 0 : dosesTaken / totalDoses;
    }
}

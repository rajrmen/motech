package org.motechproject.adherence.service;

import org.joda.time.LocalDate;
import org.motechproject.adherence.dao.AllAdherenceLogs;
import org.motechproject.adherence.domain.AdherenceLog;
import org.motechproject.adherence.domain.ErrorFunction;
import org.motechproject.util.DateUtil;
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

    public void recordDoseTaken(String externalId, boolean taken, ErrorFunction errorFunction) {
        AdherenceLog latestLog = allAdherenceLogs.findLatestLog(externalId);
        LocalDate today = DateUtil.today();
        int dosesTaken = taken ? 1 : 0;
        if (latestLog == null) {
            AdherenceLog newLog = AdherenceLog.create(externalId, today).addAdherence(dosesTaken, 1);
            allAdherenceLogs.insert(newLog);
        } else {
            AdherenceLog fillerLog = correctError(externalId, latestLog, errorFunction, today);
            latestLog = (fillerLog == null) ? latestLog : fillerLog;
            AdherenceLog newLog = latestLog.addAdherence(dosesTaken, 1);
            allAdherenceLogs.insert(newLog);
        }
    }

    public void recordAdherence(String externalId, int taken, int totalDoses, LocalDate fromDate, LocalDate toDate, ErrorFunction errorFunction) {
        AdherenceLog latestLog = allAdherenceLogs.findLatestLog(externalId);
        LocalDate today = DateUtil.today();
        if (latestLog == null) {
            AdherenceLog newLog = AdherenceLog.create(externalId, today).addAdherence(taken, totalDoses);
            newLog.setFromDate(fromDate);
            newLog.setToDate(toDate);
            allAdherenceLogs.insert(newLog);
        } else {
            AdherenceLog fillerLog = correctError(externalId, latestLog, errorFunction, today);
            latestLog = (fillerLog == null) ? latestLog : fillerLog;
            AdherenceLog newLog = latestLog.addAdherence(taken, totalDoses);
            newLog.setFromDate(fromDate);
            newLog.setToDate(toDate);
            allAdherenceLogs.insert(newLog);
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

    public double getRunningAverageAdherence(String externalId, LocalDate on) {
        AdherenceLog log = allAdherenceLogs.findByDate(externalId, on);
        if (log == null) {
            return 0;
        } else {
            return ((double) log.getDosesTaken()) / log.getTotalDoses();
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

    public void updateLatestAdherence(String externalId, int dosesTaken, int totalDoses) {
        AdherenceLog latestLog = allAdherenceLogs.findLatestLog(externalId);
        latestLog.updateDeltaDosesTaken(dosesTaken);
        latestLog.updateDeltaTotalDoses(totalDoses);
        allAdherenceLogs.update(latestLog);
    }

    private AdherenceLog correctError(String externalId, AdherenceLog latestLog, ErrorFunction errorFunction, LocalDate fromDate) {
        if (latestLog.isNotOn(fromDate.minusDays(1))) {
            AdherenceLog fillerLog = AdherenceLog.create(externalId, latestLog.getFromDate().plusDays(1), fromDate.minusDays(1));
            fillerLog = fillerLog.addAdherence(latestLog.getDosesTaken() + errorFunction.getDosesTaken(), latestLog.getTotalDoses() + errorFunction.getTotalDoses());
            allAdherenceLogs.insert(fillerLog);
            return fillerLog;
        }
        return null;
    }
}

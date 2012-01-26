package org.motechproject.adherence.service;

import org.joda.time.LocalDate;
import org.motechproject.adherence.dao.AllAdherenceLogs;
import org.motechproject.adherence.domain.AdherenceLog;
import org.motechproject.adherence.domain.ErrorFunction;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class AdherenceService {

    public static String ERROR_CORRECTION = "error_Correction";
    private AllAdherenceLogs allAdherenceLogs;

    @Autowired
    public AdherenceService(AllAdherenceLogs allAdherenceLogs) {
        this.allAdherenceLogs = allAdherenceLogs;
    }

    public void recordDoseTaken(String externalId, String conceptId, boolean taken, ErrorFunction errorFunction, Map<String, Object> meta) {
        AdherenceLog latestLog = allAdherenceLogs.findLatestLog(externalId, conceptId);
        LocalDate today = DateUtil.today();
        int dosesTaken = taken ? 1 : 0;
        if (latestLog == null) {
            AdherenceLog newLog = AdherenceLog.create(externalId, conceptId, today).addAdherence(dosesTaken, 1);
            newLog.setMeta(meta);
            allAdherenceLogs.insert(newLog);
        } else {
            AdherenceLog fillerLog = correctError(externalId, conceptId, latestLog, errorFunction, today);
            latestLog = (fillerLog == null) ? latestLog : fillerLog;
            AdherenceLog newLog = latestLog.addAdherence(dosesTaken, 1);
            newLog.setMeta(meta);
            allAdherenceLogs.insert(newLog);
        }
    }

    public void recordAdherence(String externalId, String conceptId, int taken, int totalDoses, LocalDate logDate, ErrorFunction errorFunction, Map<String, Object> meta) {
        recordAdherence(externalId, conceptId, taken, totalDoses, logDate, logDate, errorFunction, meta);
    }

    public void recordAdherence(String externalId, String conceptId, int taken, int totalDoses, LocalDate fromDate, LocalDate toDate, ErrorFunction errorFunction, Map<String, Object> meta) {
        AdherenceLog latestLog = allAdherenceLogs.findLatestLog(externalId, conceptId);
        LocalDate today = DateUtil.today();
        if (latestLog == null) {
            AdherenceLog newLog = AdherenceLog.create(externalId, conceptId, today).addAdherence(taken, totalDoses);
            newLog.setFromDate(fromDate);
            newLog.setToDate(toDate);
            newLog.setMeta(meta);
            allAdherenceLogs.insert(newLog);
        } else {
            AdherenceLog fillerLog = correctError(externalId, conceptId, latestLog, errorFunction, today);
            latestLog = (fillerLog == null) ? latestLog : fillerLog;
            AdherenceLog newLog = latestLog.addAdherence(taken, totalDoses);
            newLog.setFromDate(fromDate);
            newLog.setToDate(toDate);
            newLog.setMeta(meta);
            allAdherenceLogs.insert(newLog);
        }
    }

    public double getRunningAverageAdherence(String externalId, String conceptId) {
        AdherenceLog latestLog = allAdherenceLogs.findLatestLog(externalId, conceptId);
        if (latestLog == null) {
            return 0;
        } else {
            return ((double) latestLog.getDosesTaken()) / latestLog.getTotalDoses();
        }
    }

    public double getRunningAverageAdherence(String externalId, String conceptId, LocalDate on) {
        AdherenceLog log = allAdherenceLogs.findByDate(externalId, conceptId, on);
        if (log == null) {
            return 0;
        } else {
            return ((double) log.getDosesTaken()) / log.getTotalDoses();
        }
    }

    public double getDeltaAdherence(String externalId, String conceptId) {
        AdherenceLog latestLog = allAdherenceLogs.findLatestLog(externalId, conceptId);
        if (latestLog == null) {
            return 0;
        } else {
            return ((double) latestLog.getDeltaDosesTaken()) / latestLog.getDeltaTotalDoses();
        }
    }

    public double getDeltaAdherence(String externalId, String conceptId, LocalDate fromDate, LocalDate toDate) {
        List<AdherenceLog> logs = allAdherenceLogs.findLogsBetween(externalId, conceptId, fromDate, toDate);
        double dosesTaken = 0;
        double totalDoses = 0;
        for (AdherenceLog adherenceLog : logs) {
            dosesTaken += adherenceLog.getDeltaDosesTaken();
            totalDoses += adherenceLog.getDeltaTotalDoses();
        }
        return totalDoses == 0 ? 0 : dosesTaken / totalDoses;
    }

    public void updateLatestAdherence(String externalId, String conceptId, int dosesTaken, int totalDoses) {
        AdherenceLog latestLog = allAdherenceLogs.findLatestLog(externalId, conceptId);
        latestLog.updateDeltaDosesTaken(dosesTaken);
        latestLog.updateDeltaTotalDoses(totalDoses);
        allAdherenceLogs.update(latestLog);
    }

    public LocalDate getLatestAdherenceDate(String externalId, String conceptId) {
        AdherenceLog latestAdherenceLog = allAdherenceLogs.findLatestLog(externalId, conceptId);
        if (latestAdherenceLog == null)
            return null;
        else
            return latestAdherenceLog.getToDate();
    }

    public List<AdherenceLog> rollBack(String externalId, String conceptId, LocalDate tillDate) {
        List<AdherenceLog> adherenceLogs = allAdherenceLogs.findLogsBetween(externalId, conceptId, tillDate.plusDays(1), DateUtil.today());
        List<AdherenceLog> removedLogs = new ArrayList<AdherenceLog>();
        for (AdherenceLog adherenceLog : adherenceLogs) {
            if (adherenceLog.cutBy(tillDate)) {
                adherenceLog.setToDate(tillDate);
                allAdherenceLogs.update(adherenceLog);
            } else {
                allAdherenceLogs.remove(adherenceLog);
                removedLogs.add(adherenceLog);
            }
        }
        return removedLogs;
    }

    public Map<String, Object> getMetaOn(String externalId, String conceptId, LocalDate date) {
        AdherenceLog latestLog = allAdherenceLogs.findByDate(externalId, conceptId, date);
        if (latestLog == null) {
            return Collections.<String, Object>emptyMap();
        } else {
            return latestLog.getMeta() == null ? Collections.<String, Object>emptyMap() : latestLog.getMeta();
        }
    }

    private AdherenceLog correctError(String externalId, String conceptId, AdherenceLog latestLog, ErrorFunction errorFunction, LocalDate fromDate) {
        if (latestLog.isNotOn(fromDate.minusDays(1))) {
            AdherenceLog fillerLog = AdherenceLog.create(externalId, conceptId, latestLog.getFromDate().plusDays(1), fromDate.minusDays(1));
            fillerLog = fillerLog.addAdherence(latestLog.getDosesTaken() + errorFunction.getDosesTaken(), latestLog.getTotalDoses() + errorFunction.getTotalDoses());
            fillerLog.putMeta(ERROR_CORRECTION, true);
            allAdherenceLogs.insert(fillerLog);
            return fillerLog;
        }
        return null;
    }
}

package org.motechproject.adherence.domain;

import org.apache.commons.lang.StringUtils;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.LocalDate;
import org.motechproject.model.MotechBaseDataObject;

import java.util.Map;

@TypeDiscriminator("doc.type === 'AdherenceLog'")
public class AdherenceLog extends MotechBaseDataObject {

    public static String GENERIC_CONCEPT_ID = null;

    protected String externalId;
    protected int dosesTaken;
    protected int totalDoses;
    protected LocalDate fromDate;
    protected LocalDate toDate;
    protected int deltaDosesTaken;
    protected int deltaTotalDoses;
    protected String conceptId = GENERIC_CONCEPT_ID;
    private Map<String, Object> meta;

    public AdherenceLog() {
    }

    public AdherenceLog(AdherenceLog that, LocalDate fromDate, LocalDate toDate) {
        this(that);
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public AdherenceLog(AdherenceLog that) {
        this.externalId = that.externalId;
        this.conceptId = that.conceptId;
        this.dosesTaken = that.dosesTaken;
        this.totalDoses = that.totalDoses;
        this.fromDate = that.fromDate;
        this.toDate = that.toDate;
        this.deltaDosesTaken = that.deltaDosesTaken;
        this.deltaTotalDoses = that.deltaTotalDoses;
    }

    public static AdherenceLog create(String externalId, String conceptId, LocalDate date) {
        return create(externalId, conceptId, date, date);
    }

    public static AdherenceLog create(String externalId, String conceptId, LocalDate fromDate, LocalDate toDate) {
        AdherenceLog newLog = new AdherenceLog();
        newLog.fromDate = fromDate;
        newLog.toDate = toDate;
        newLog.externalId = externalId;
        newLog.conceptId = conceptId;
        return newLog;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public int getDosesTaken() {
        return dosesTaken;
    }

    public void setDosesTaken(int dosesTaken) {
        this.dosesTaken = dosesTaken;
    }

    public int getTotalDoses() {
        return totalDoses;
    }

    public void setTotalDoses(int totalDoses) {
        this.totalDoses = totalDoses;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public void setDateRange(LocalDate fromDate, LocalDate toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public int getDeltaDosesTaken() {
        return deltaDosesTaken;
    }

    public void setDeltaDosesTaken(int deltaDosesTaken) {
        this.deltaDosesTaken = deltaDosesTaken;
    }

    public int getDeltaTotalDoses() {
        return deltaTotalDoses;
    }

    public void setDeltaTotalDoses(int deltaTotalDoses) {
        this.deltaTotalDoses = deltaTotalDoses;
    }

    public String getConceptId() {
        return conceptId;
    }

    public void setConceptId(String conceptId) {
        this.conceptId = conceptId;
    }

    public Map<String, Object> getMeta() {
        return meta;
    }

    public void setMeta(Map<String, Object> meta) {
        this.meta = meta;
    }

    public AdherenceLog cut(AdherenceLog otherLog) {
        otherLog.setFromDate(this.getToDate().plusDays(1));
        return otherLog;
    }

    public boolean overlaps(AdherenceLog that) {
        return !this.toDate.isBefore(that.fromDate) && that.toDate.isAfter(this.toDate);
    }

    public boolean encloses(AdherenceLog entity) {
        return !this.fromDate.isAfter(entity.fromDate) && !this.toDate.isBefore(entity.toDate);
    }

    public AdherenceLog addAdherence(int dosesTaken, int totalDoses) {
        AdherenceLog newLog = new AdherenceLog(this);
        newLog.setDosesTaken(this.dosesTaken + dosesTaken);
        newLog.setTotalDoses(this.totalDoses + totalDoses);
        newLog.setDeltaDosesTaken(dosesTaken);
        newLog.setDeltaTotalDoses(totalDoses);
        return newLog;
    }

    public boolean isNotOn(LocalDate date) {
        return date.isAfter(this.toDate);
    }

    public void updateDeltaDosesTaken(int deltaDosesTaken) {
        this.dosesTaken -= this.deltaDosesTaken;
        this.dosesTaken += deltaDosesTaken;
        this.deltaDosesTaken = deltaDosesTaken;
    }

    public void updateDeltaTotalDoses(int deltaTotalDoses) {
        this.totalDoses -= this.deltaTotalDoses;
        this.totalDoses += deltaTotalDoses;
        this.deltaTotalDoses = deltaTotalDoses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AdherenceLog that = (AdherenceLog) o;

        if (StringUtils.isEmpty(getId())) return StringUtils.isEmpty(that.getId());
        if (!getId().equals(that.getId())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}

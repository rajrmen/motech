package org.motechproject.adherence.Service;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.adherence.dao.AllAdherenceLogs;
import org.motechproject.adherence.domain.AdherenceLog;
import org.motechproject.util.DateUtil;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AdherenceServiceTest {

    @Mock
    private AllAdherenceLogs allAdherenceLogs;
    private AdherenceService adherenceService;
    private String externalId;

    @Before
    public void setUp() {
        initMocks(this);
        adherenceService = new AdherenceService(allAdherenceLogs);
        externalId = "externalId";
    }

    @Test
    public void shouldStartRecordingAdherence() {
        when(allAdherenceLogs.findLatestLog(externalId)).thenReturn(null);

        adherenceService.recordDoseTaken(externalId, true);
        ArgumentCaptor<AdherenceLog> logCapture = ArgumentCaptor.forClass(AdherenceLog.class);
        verify(allAdherenceLogs).add(logCapture.capture());
        assertEquals(1, logCapture.getValue().getDosesTaken());
        assertEquals(1, logCapture.getValue().getTotalDoses());
    }

    @Test
    public void shouldRecordDoseTaken() {
        AdherenceLog existingLog = AdherenceLog.create(externalId);
        existingLog.setDosesTaken(1);
        existingLog.setTotalDoses(1);
        when(allAdherenceLogs.findLatestLog(externalId)).thenReturn(existingLog);

        adherenceService.recordDoseTaken(externalId, true);
        ArgumentCaptor<AdherenceLog> logCapture = ArgumentCaptor.forClass(AdherenceLog.class);
        verify(allAdherenceLogs).add(logCapture.capture());
        assertEquals(2, logCapture.getValue().getDosesTaken());
        assertEquals(2, logCapture.getValue().getTotalDoses());
    }

    @Test
    public void shouldRecordDoseNotTaken() {
        when(allAdherenceLogs.findLatestLog(externalId)).thenReturn(null);

        adherenceService.recordDoseTaken(externalId, false);
        ArgumentCaptor<AdherenceLog> logCapture = ArgumentCaptor.forClass(AdherenceLog.class);
        verify(allAdherenceLogs).add(logCapture.capture());
        assertEquals(0, logCapture.getValue().getDosesTaken());
        assertEquals(1, logCapture.getValue().getTotalDoses());
    }

    @Test
    public void shouldRecordAdherenceBetweenARange() {
        LocalDate fromDate = DateUtil.newDate(2011, 12, 1);
        LocalDate toDate = DateUtil.newDate(2011, 12, 31);
        when(allAdherenceLogs.findLatestLog(externalId)).thenReturn(null);

        adherenceService.recordAdherence(externalId, 1, 1, fromDate, toDate);
        ArgumentCaptor<AdherenceLog> logCapture = ArgumentCaptor.forClass(AdherenceLog.class);
        verify(allAdherenceLogs).add(logCapture.capture());
        assertEquals(fromDate, logCapture.getValue().getFromDate());
        assertEquals(toDate, logCapture.getValue().getToDate());
    }

    @Test
    public void shouldReportRunningAverageAdherence() {
        AdherenceLog existingLog = AdherenceLog.create(externalId);
        existingLog.setDosesTaken(1);
        existingLog.setTotalDoses(2);
        when(allAdherenceLogs.findLatestLog(externalId)).thenReturn(existingLog);

        assertEquals(0.5, adherenceService.getRunningAverageAdherence(externalId));
    }

    @Test
    public void shouldReportRunningAverageAdherenceOnGivenDate() {
        AdherenceLog existingLog = AdherenceLog.create(externalId);
        existingLog.setDosesTaken(1);
        existingLog.setTotalDoses(2);
        LocalDate date = DateUtil.newDate(2011, 12, 1);
        when(allAdherenceLogs.findByDate(externalId, date)).thenReturn(existingLog);

        assertEquals(0.5, adherenceService.getRunningAverageAdherence(externalId, date));
    }

    @Test
    public void shouldReportDeltaAdherence() {
        AdherenceLog existingLog = AdherenceLog.create(externalId);
        existingLog.setDosesTaken(1);
        existingLog.setTotalDoses(2);
        existingLog.setDeltaDosesTaken(1);
        existingLog.setDeltaTotalDoses(4);
        when(allAdherenceLogs.findLatestLog(externalId)).thenReturn(existingLog);

        assertEquals(0.25, adherenceService.getDeltaAdherence(externalId));
    }

    @Test
    public void shouldReportDeltaAdherenceOverDateRange() {
        AdherenceLog log = AdherenceLog.create(externalId);
        log.setDeltaDosesTaken(1);
        log.setDeltaTotalDoses(1);
        AdherenceLog secondLog = AdherenceLog.create(externalId);
        secondLog.setDeltaDosesTaken(0);
        secondLog.setDeltaTotalDoses(1);

        LocalDate fromDate = DateUtil.newDate(2011, 12, 1);
        LocalDate toDate = DateUtil.newDate(2011, 12, 31);

        when(allAdherenceLogs.findLogsBetween(externalId, fromDate, toDate)).thenReturn(Arrays.asList(log, secondLog));
        assertEquals(0.5, adherenceService.getDeltaAdherence(externalId, fromDate, toDate));
    }
}

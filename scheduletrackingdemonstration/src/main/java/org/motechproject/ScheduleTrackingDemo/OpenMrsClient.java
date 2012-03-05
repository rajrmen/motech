package org.motechproject.ScheduleTrackingDemo;

import java.util.Date;

import org.joda.time.DateTime;
import org.motechproject.mrs.model.MRSPatient;

public interface OpenMrsClient {

	boolean hasConcept(String patientId, String conceptName);
	public void printValues(String externalID, String conceptName);
	public DateTime lastTimeFulfilledDateTimeObs(String patientId, String conceptName);
	public MRSPatient getPatientByMotechId(String patientId);
	void savePatient(MRSPatient patient);
	void addEncounterForPatient(String motechId, String conceptName, Date observedDate);
}

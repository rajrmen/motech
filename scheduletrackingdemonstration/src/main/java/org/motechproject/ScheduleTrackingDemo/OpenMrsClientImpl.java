package org.motechproject.ScheduleTrackingDemo;

import java.util.List;
import java.util.Set;

import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.motechproject.openmrs.services.OpenMRSEncounterAdapter;
import org.motechproject.openmrs.services.OpenMRSObservationAdapter;
import org.motechproject.openmrs.services.OpenMRSPatientAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class OpenMrsClientImpl implements OpenMrsClient {
	private static Logger logger = LoggerFactory.getLogger(OpenMrsClientImpl.class);
	private OpenMRSEncounterAdapter encounterAdapter;
	private OpenMRSPatientAdapter patientAdapter;
	private OpenMRSObservationAdapter observationAdapter;

	@Autowired
	public OpenMrsClientImpl(OpenMRSEncounterAdapter encounterAdapter, OpenMRSPatientAdapter patientAdapter, OpenMRSObservationAdapter observationAdapter) {
		this.encounterAdapter = encounterAdapter;
		this.patientAdapter = patientAdapter;
		this.observationAdapter = observationAdapter;
	}
	
	@LoginAsAdmin
	@ApiSession
	public boolean hasConcept(String patientId, String conceptName) {
		logger.debug(conceptName);
		List<MRSObservation> observationList = observationAdapter.getMRSObservationsByMotechPatientIdAndConceptName(patientId, conceptName);
		boolean found = false;
		if (observationList.size() > 0) {
			found = true;
			logger.debug("Found:" + found);
		} else {
			logger.debug("No encounter found for: " + patientId);
		}
		return found;
		
	}
}

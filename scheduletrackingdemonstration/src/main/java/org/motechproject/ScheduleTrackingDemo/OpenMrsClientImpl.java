package org.motechproject.ScheduleTrackingDemo;

import java.util.Set;

import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.motechproject.openmrs.services.OpenMRSEncounterAdapter;
import org.motechproject.openmrs.services.OpenMRSPatientAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class OpenMrsClientImpl implements OpenMrsClient {
	private static Logger logger = LoggerFactory.getLogger(OpenMrsClientImpl.class);
	private OpenMRSEncounterAdapter encounterAdapter;
	private OpenMRSPatientAdapter patientAdapter;

	@Autowired
	public OpenMrsClientImpl(OpenMRSEncounterAdapter encounterAdapter, OpenMRSPatientAdapter patientAdapter) {
		this.encounterAdapter = encounterAdapter;
		this.patientAdapter = patientAdapter;
	}
	
	@LoginAsAdmin
	@ApiSession
	public boolean hasConcept(String patientId, String conceptName) {
		logger.debug(conceptName);
		MRSEncounter encounter = encounterAdapter.getLatestEncounterByPatientMotechId(patientId, "ADULTINITIAL");
		if (encounter == null) {
			logger.debug("No encounter found for: " + patientId);
			return false;
		}
		Set<MRSObservation> observations = encounter.getObservations();
		boolean found = false;
		for (MRSObservation obs : observations) {
			if (obs.getConceptName().equals(conceptName)) {
				found = true;
			}
		}

		logger.debug("Found:" + found);
		return found;
	}
}

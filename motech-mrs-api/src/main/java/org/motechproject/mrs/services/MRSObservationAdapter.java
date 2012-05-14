package org.motechproject.mrs.services;

import java.util.List;

import org.motechproject.mrs.model.MRSObservation;

public interface MRSObservationAdapter {

	public List<MRSObservation> getMRSObservationsByMotechPatientIdAndConceptName(String motechId, String conceptName);
	
}

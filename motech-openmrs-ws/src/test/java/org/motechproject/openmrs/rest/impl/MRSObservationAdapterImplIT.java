package org.motechproject.openmrs.rest.impl;

import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.mrs.services.MRSEncounterAdapter;
import org.motechproject.openmrs.rest.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationOpenMrsWS.xml" })
public class MRSObservationAdapterImplIT {

	@Autowired
	AdapterHelper adapterHelper;

	@Autowired
	MRSEncounterAdapter encounterAdapter;

	@Autowired
	MRSObservationAdapterImpl obsAdapter;

	private static final String ENCOUNTER_TYPE = "ADULTINITIAL";

	@Test
	public void shouldRetrieveObservationByType() throws HttpException, URISyntaxException {
		MRSFacility facility = null;
		MRSPatient patient = null;
		String tempConceptUuid = null;
		String tempConceptUuid2 = null;
		MRSUser creator = null;
		MRSEncounter encounter = null;

		try {
			facility = adapterHelper.createTemporaryLocation();
			patient = adapterHelper.createTemporaryPatient(TestUtils.MOTECH_ID_1, TestUtils.makePerson(), facility);
			tempConceptUuid = adapterHelper.createTemporaryConcept(AdapterHelper.TEST_CONCEPT_NAME);
			tempConceptUuid2 = adapterHelper.createTemporaryConcept(AdapterHelper.TEST_CONCEPT_NAME + 2);
			creator = adapterHelper.createTemporaryProvider();

			Set<MRSObservation> obs = new HashSet<MRSObservation>();
			obs.add(new MRSObservation(Calendar.getInstance().getTime(), AdapterHelper.TEST_CONCEPT_NAME, "Test Value"));
			obs.add(new MRSObservation(Calendar.getInstance().getTime(), AdapterHelper.TEST_CONCEPT_NAME + 2, "Test Value"));

			encounter = new MRSEncounter(creator.getPerson().getId(), creator.getId(), facility.getId(),
					TestUtils.CURRENT_DATE, patient.getId(), obs, ENCOUNTER_TYPE);

			encounter = encounterAdapter.createEncounter(encounter);

			List<MRSObservation> persistedObs = obsAdapter.getMRSObservationsByMotechPatientIdAndConceptName(
					TestUtils.MOTECH_ID_1, AdapterHelper.TEST_CONCEPT_NAME);

			assertEquals(1, persistedObs.size());
		} finally {
			adapterHelper.deleteEncounter(encounter);
			adapterHelper.deleteConcept(tempConceptUuid);
			adapterHelper.deleteConcept(tempConceptUuid2);
			adapterHelper.deletePatient(patient);
			adapterHelper.deleteUser(creator);
			adapterHelper.deleteFacility(facility);
		}
	}
}

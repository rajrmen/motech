package org.motechproject.openmrs.ws.impl.it;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.services.EncounterAdapter;
import org.motechproject.mrs.services.FacilityAdapter;
import org.motechproject.mrs.services.PatientAdapter;
import org.motechproject.mrs.services.UserAdapter;
import org.motechproject.openmrs.model.OpenMRSEncounter;
import org.motechproject.openmrs.model.OpenMRSEncounter.MRSEncounterBuilder;
import org.motechproject.openmrs.model.OpenMRSFacility;
import org.motechproject.openmrs.model.OpenMRSObservation;
import org.motechproject.openmrs.model.OpenMRSPatient;
import org.motechproject.openmrs.model.OpenMRSPerson;
import org.motechproject.openmrs.model.OpenMRSProvider;
import org.motechproject.openmrs.model.OpenMRSUser;
import org.motechproject.openmrs.ws.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractEncounterIT {
    @Autowired
    private UserAdapter userAdapter;

    @Autowired
    private FacilityAdapter facilityAdapter;

    @Autowired
    private EncounterAdapter encounterAdapter;

    @Autowired
    private PatientAdapter patientAdapter;

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    @Test
    public void shouldCreateEncounter() throws UserAlreadyExistsException, HttpException, ParseException {
        OpenMRSUser user = (OpenMRSUser) userAdapter.getUserByUserName("chuck");
        String obsDate = "2012-09-05";
        OpenMRSObservation ob = new OpenMRSObservation<>(format.parse(obsDate), "Motech Concept", "Test");
        Set<OpenMRSObservation> obs = new HashSet<>();
        obs.add(ob);
        
        OpenMRSPerson person = user.getPerson();
        OpenMRSProvider provider = new OpenMRSProvider(person);
        provider.setProviderId(person.getPersonId());

        OpenMRSFacility facility = (OpenMRSFacility) facilityAdapter.getFacilities("Clinic 1").get(0);
        OpenMRSPatient patient = (OpenMRSPatient) patientAdapter.getPatientByMotechId("700");
        OpenMRSEncounter encounter = new MRSEncounterBuilder().withDate(format.parse(obsDate))
                .withEncounterType("ADULTINITIAL").withFacility(facility).withObservations(obs).withPatient(patient)
                .withProvider(provider).build();

        OpenMRSEncounter saved = (OpenMRSEncounter) encounterAdapter.createEncounter(encounter);
        assertNotNull(saved);
        assertNotNull(saved.getId());
    }

    @Test
    public void shouldGetLatestEncounter() {
        OpenMRSEncounter encounter = (OpenMRSEncounter) encounterAdapter.getLatestEncounterByPatientMotechId("700", "ADULTINITIAL");

        assertNotNull(encounter);
        assertEquals("2012-09-07", format.format(encounter.getDate()));
    }

}

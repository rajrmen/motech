package org.motechproject.openmrs.rest.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.openmrs.rest.HttpException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationOpenMrsWS.xml" })
public class MRSPatientAdapterImplIT extends AbstractAdapterImplIT {

	private static final String MOTECH_ID_2 = "200-09";
	private static final String MOTECH_ID_3 = "300-10";
	private static final String TEMPORARY_ATTRIBUTE_VALUE = "Temporary Value";
	private static final String TEMPORARY_ATTRIBUTE_TYPE_NAME = "Temporary Attribute Type";

	@Test
	public void shouldCreatePatientWithAttributes() throws HttpException, URISyntaxException {
		String attributeUuid = null;
		MRSFacility facility = null;
		MRSPatient patient = null;
		try {
			facility = createTemporaryLocation();
			attributeUuid = createTemporaryAttributeType(TEMPORARY_ATTRIBUTE_TYPE_NAME);
			
			MRSPerson person = makePerson();
			addAttributeToPatient(person, TEMPORARY_ATTRIBUTE_TYPE_NAME);
			
			patient = createTemporaryPatient(MOTECH_ID_1, person, facility);
			
			MRSPatient persistedPatient = patientAdapter.getPatient(patient.getId());
			MRSPerson persistedPerson = persistedPatient.getPerson();
			
			assertNotNull(persistedPatient);
			assertEquals(MOTECH_ID_1, persistedPatient.getMotechId());
			
			assertEquals(FIRST_NAME, persistedPerson.getFirstName());
			assertEquals(MIDDLE_NAME, persistedPerson.getMiddleName());
			assertEquals(LAST_NAME, persistedPerson.getLastName());
			assertEquals(GENDER, persistedPerson.getGender());
			assertEquals(ADDRESS, persistedPerson.getAddress());
			assertEquals(currentDate, persistedPerson.getDateOfBirth());
			assertEquals(1, persistedPerson.getAttributes().size());
			assertEquals(TEMPORARY_ATTRIBUTE_TYPE_NAME, persistedPerson.getAttributes().get(0).name());
			assertEquals(TEMPORARY_ATTRIBUTE_VALUE, persistedPerson.getAttributes().get(0).value());
		} finally {
			deletePatient(patient);
			deleteAttributeType(attributeUuid);
			deleteFacility(facility);
		}
	}

	private void addAttributeToPatient(MRSPerson person, String attributeName) {
		person.addAttribute(new Attribute(attributeName, TEMPORARY_ATTRIBUTE_VALUE));	
	}

	private String createTemporaryAttributeType(String attributeName) throws HttpException, URISyntaxException {
		ObjectNode obj = JsonNodeFactory.instance.objectNode();
		obj.put("name", attributeName);
		obj.put("description", "Temporary Attibute Type 2");
		obj.put("format", "java.lang.String");
		
		JsonNode response = restfulClient.postForJsonNode(new URI(openmrsUrl + "/ws/rest/v1/personattributetype"), obj);
		
		return response.get("uuid").asText();
	}	
	
	
	private void deleteAttributeType(String attributeUuid) {
		restOperations.delete(openmrsUrl + "/ws/rest/v1/personattributetype/{uuid}?purge", attributeUuid);
	}

	@Test
	public void shouldFindPatientByMotechId() throws HttpException, URISyntaxException {
		MRSFacility facility = null;
		MRSPatient patient = null;
		try {
			facility = createTemporaryLocation();
			MRSPerson person = makePerson();
			patient = createTemporaryPatient(MOTECH_ID_1, person, facility);
			
			MRSPatient persistedPatient = patientAdapter.getPatientByMotechId(MOTECH_ID_1);
			assertNotNull(persistedPatient);
		} finally {
			deletePatient(patient);
			deleteFacility(facility);
		}		
	}
	
	@Test
	public void shouldSetPersonToDead() throws HttpException, URISyntaxException {
		MRSFacility facility = null;
		MRSPatient patient = null;
		try {
			facility = createTemporaryLocation();
			MRSPerson person = makePerson();
			patient = createTemporaryPatient(MOTECH_ID_1, person, facility);
			
			patientAdapter.savePatientCauseOfDeathObservation(patient.getId(), "NONE", currentDate, null);
			MRSPatient persistedPatient = patientAdapter.getPatient(patient.getId());
			
			assertTrue(persistedPatient.getPerson().isDead());
			assertEquals(currentDate, persistedPatient.getPerson().deathDate());
		} finally {
			deletePatient(patient);
			deleteFacility(facility);
		}			
	}
	
	@Test
	public void shouldFindPatientsOnSearch() throws HttpException, URISyntaxException {
		MRSFacility facility = null;
		MRSPatient patient1 = null;
		MRSPatient patient2 = null;
		MRSPatient patient3 = null;
		try {
			facility = createTemporaryLocation();
			MRSPerson person1 = makePerson();
			patient1 = createTemporaryPatient(MOTECH_ID_1, person1, facility);
			MRSPerson person2 = makePerson();
			patient2 = createTemporaryPatient(MOTECH_ID_2, person2, facility);
			MRSPerson person3 = makePerson();
			patient3 = createTemporaryPatient(MOTECH_ID_3, person3, facility);

			List<MRSPatient> patients = patientAdapter.search(FIRST_NAME, null);
			
			assertEquals(3, patients.size());
			assertEquals(MOTECH_ID_1, patients.get(0).getMotechId());
			assertEquals(MOTECH_ID_2, patients.get(1).getMotechId());
			assertEquals(MOTECH_ID_3, patients.get(2).getMotechId());
		} finally {
			deletePatient(patient1);
			deletePatient(patient2);
			deletePatient(patient3);
			deleteFacility(facility);
		}			
	}
	
	@Test
	public void shouldFindPatientsContainingMotechId() throws HttpException, URISyntaxException {
		MRSFacility facility = null;
		MRSPatient patient1 = null;
		MRSPatient patient2 = null;
		MRSPatient patient3 = null;
		try {
			facility = createTemporaryLocation();
			MRSPerson person1 = makePerson();
			patient1 = createTemporaryPatient(MOTECH_ID_1, person1, facility);
			MRSPerson person2 = makePerson();
			patient2 = createTemporaryPatient(MOTECH_ID_2, person2, facility);
			MRSPerson person3 = makePerson();
			patient3 = createTemporaryPatient(MOTECH_ID_3, person3, facility);

			List<MRSPatient> patients = patientAdapter.search(FIRST_NAME, "200");
			
			assertEquals(2, patients.size());
			assertEquals(MOTECH_ID_1, patients.get(0).getMotechId());
			assertEquals(MOTECH_ID_2, patients.get(1).getMotechId());
		} finally {
			deletePatient(patient1);
			deletePatient(patient2);
			deletePatient(patient3);
			deleteFacility(facility);
		}			
	}		
	
	@Test
	public void shouldUpdatePatient() throws HttpException, URISyntaxException {
		String attributeUuid1 = null;
		String attributeUuid2 = null;
		MRSFacility facility = null;
		MRSPatient patient = null;
		try {
			facility = createTemporaryLocation();
			attributeUuid1 = createTemporaryAttributeType(TEMPORARY_ATTRIBUTE_TYPE_NAME);
			attributeUuid2 = createTemporaryAttributeType(TEMPORARY_ATTRIBUTE_TYPE_NAME + 2);
			
			MRSPerson person = makePerson();
			addAttributeToPatient(person, TEMPORARY_ATTRIBUTE_TYPE_NAME);
			
			patient = createTemporaryPatient(MOTECH_ID_1, person, facility);	
			
			patient.getPerson().firstName("Changed First");
			patient.getPerson().lastName("Changed Last");
			patient.getPerson().getAttributes().remove(0);
			addAttributeToPatient(person, TEMPORARY_ATTRIBUTE_TYPE_NAME + 2);
			
			patientAdapter.updatePatient(patient);
			
			MRSPatient persistedPatient = patientAdapter.getPatient(patient.getId());
			MRSPerson persistedPerson = persistedPatient.getPerson();
			assertEquals("Changed First", persistedPerson.getFirstName());
			assertEquals("Changed Last", persistedPerson.getLastName());
			assertEquals(1, persistedPerson.getAttributes().size());
			assertEquals(TEMPORARY_ATTRIBUTE_TYPE_NAME + 2, persistedPerson.getAttributes().get(0).name());
		} finally {
			deletePatient(patient);
			deleteAttributeType(attributeUuid1);
			deleteAttributeType(attributeUuid2);
			deleteFacility(facility);
		}	
	}
}

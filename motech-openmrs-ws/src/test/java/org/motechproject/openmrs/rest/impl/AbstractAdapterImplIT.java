package org.motechproject.openmrs.rest.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.services.MRSFacilityAdapter;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.openmrs.rest.HttpException;
import org.motechproject.openmrs.rest.RestfulClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestOperations;

public class AbstractAdapterImplIT {

	protected static final String MOTECH_ID_1 = "200-08";
	protected static final String ADDRESS = "5 Main St";
	protected static final String GENDER = "M";
	protected static final String LAST_NAME = "Trump";
	protected static final String MIDDLE_NAME = "E";
	protected static final String FIRST_NAME = "Donald";
	protected final Date currentDate;
	
	@Autowired
    protected MRSPatientAdapter patientAdapter;
	
	@Autowired
    MRSFacilityAdapter facilityAdapter;
	
	@Autowired
    protected RestfulClient restfulClient;
	
	@Autowired
    protected RestOperations restOperations;
	
	@Value("${openmrs.url}")
    protected String openmrsUrl;

	public AbstractAdapterImplIT() {
		Calendar instance = Calendar.getInstance();
		instance.set(Calendar.HOUR_OF_DAY, 0);
		instance.set(Calendar.MINUTE, 0);
		instance.set(Calendar.SECOND, 0);
		instance.set(Calendar.MILLISECOND, 0);
		
		currentDate = instance.getTime();
	}

	protected void deleteFacility(MRSFacility facility) {
		restOperations.delete(openmrsUrl + "/ws/rest/v1/location/{uuid}?purge", facility.getId());
    }

	protected void deletePatient(MRSPatient patient) {
		restOperations.delete(openmrsUrl + "/ws/rest/v1/patient/{uuid}?purge", patient.getId());
    }

	protected MRSPatient createTemporaryPatient(String motechId, MRSPerson person, MRSFacility facility) {
    	MRSPatient patient = new MRSPatient(motechId, person, facility);
    	
    	return patientAdapter.savePatient(patient);
    }

	protected MRSPerson makePerson() {
    	MRSPerson person = new MRSPerson().firstName(FIRST_NAME).middleName(MIDDLE_NAME)
    			.lastName(LAST_NAME).gender(GENDER).address(ADDRESS)
    			.dateOfBirth(currentDate);
    	return person;
    }

	protected MRSFacility createTemporaryLocation() throws HttpException, URISyntaxException {
    	ObjectNode obj = JsonNodeFactory.instance.objectNode();
    	obj.put("name", "Temporary Location");
    	obj.put("description", "Temporary Location");
    	JsonNode result = restfulClient.postForJsonNode(new URI(openmrsUrl + "/ws/rest/v1/location"), obj);
    	
    	return new MRSFacility(result.get("uuid").asText());
    }
}
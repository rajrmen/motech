package org.motechproject.openmrs.rest.impl;

import java.net.URI;
import java.net.URISyntaxException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.mrs.services.MRSFacilityAdapter;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.openmrs.rest.HttpException;
import org.motechproject.openmrs.rest.RestfulClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

@Component
public class AdapterHelper {

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

	protected MRSFacility createTemporaryLocation() throws HttpException, URISyntaxException {
    	ObjectNode obj = JsonNodeFactory.instance.objectNode();
    	obj.put("name", "Temporary Location");
    	obj.put("description", "Temporary Location");
    	JsonNode result = restfulClient.postForJsonNode(new URI(openmrsUrl + "/ws/rest/v1/location"), obj);
    	
    	return new MRSFacility(result.get("uuid").asText());
    }

	public void deleteUser(MRSUser user) {
		if (user == null || user.getId() == null) return;
		restOperations.delete(openmrsUrl + "/ws/rest/v1/user/{uuid}?purge", user.getId());
    }
}
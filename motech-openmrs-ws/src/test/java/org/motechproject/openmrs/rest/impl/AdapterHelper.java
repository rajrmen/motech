package org.motechproject.openmrs.rest.impl;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.motechproject.mrs.model.MRSEncounter;
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

	static final String CONCEPT_PATH = "/ws/rest/v1/concept";
	static final String TEST_CONCEPT_NAME = "Test Concept";
	
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
    	
    	return new MRSFacility(result.get("uuid").getValueAsText());
    }

	public void deleteUser(MRSUser user) throws HttpException, URISyntaxException {
		if (user == null || user.getId() == null) return;
		restfulClient.deleteEntity(new URI(openmrsUrl + "/ws/rest/v1/user/" + user.getId() + "?purge"));
		restfulClient.deleteEntity(new URI(openmrsUrl + "/ws/rest/v1/person/" + user.getPerson().getId() + "?purge"));
    }

	public String createTemporaryConcept(String conceptName) throws URISyntaxException, HttpException {
		if (StringUtils.isEmpty(conceptName)) {
			conceptName = TEST_CONCEPT_NAME;
		}
		
		URI uri = new URI(openmrsUrl + CONCEPT_PATH);
		ObjectNode conceptObj = JsonNodeFactory.instance.objectNode();
		
		ArrayNode names = JsonNodeFactory.instance.arrayNode();
		ObjectNode name = JsonNodeFactory.instance.objectNode();
		name.put("name", conceptName);
		name.put("locale", "en");
		name.put("conceptNameType", "FULLY_SPECIFIED");
		names.add(name);
		
		conceptObj.put("names", names);
		conceptObj.put("datatype", "Text");
		conceptObj.put("conceptClass", "Test");
		JsonNode result = restfulClient.postForJsonNode(uri, conceptObj);

		return result.get("uuid").getValueAsText();
	}

	public MRSUser createTemporaryProvider() throws URISyntaxException, HttpException {
		ObjectNode person = JsonNodeFactory.instance.objectNode();
		person.put("birthdate", "1970-01-01");
		person.put("gender", "M");
		ArrayNode node = JsonNodeFactory.instance.arrayNode();
		ObjectNode preferredName = JsonNodeFactory.instance.objectNode();
		preferredName.put("givenName", "Troy");
		preferredName.put("familyName", "Parks");
		node.add(preferredName);
		person.put("names", node);

		URI personUri = new URI(openmrsUrl + "/ws/rest/v1/person");
		JsonNode response = restfulClient.postForJsonNode(personUri, person);
		String personUuid = response.get("uuid").getValueAsText();

		ObjectNode userNode = JsonNodeFactory.instance.objectNode();
		userNode.put("username", "troy");
		userNode.put("password", "Testing123");
		userNode.put("person", personUuid);

		URI userUri = new URI(openmrsUrl + "/ws/rest/v1/user");
		response = restfulClient.postForJsonNode(userUri, userNode);

		return new MRSUser().id(response.get("uuid").getValueAsText()).person(new MRSPerson().id(personUuid));
	}

	public void deleteEncounter(MRSEncounter persistedEncounter) throws HttpException, URISyntaxException {
		if (persistedEncounter == null) return;
		restfulClient.deleteEntity(new URI(openmrsUrl + "/ws/rest/v1/encounter/" + persistedEncounter.getId() + "?purge"));
	}	
	
	public void deleteConcept(String tempConceptUuid) throws HttpException, URISyntaxException {
		if (tempConceptUuid == null) return;
		restfulClient.deleteEntity(new URI(openmrsUrl + CONCEPT_PATH + "/" + tempConceptUuid + "?purge"));
	}
}
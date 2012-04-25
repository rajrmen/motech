package org.motechproject.openmrs.rest.url;

import java.net.URI;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;

@Component
public class OpenMrsEncounterUrlHolder implements InitializingBean {

	private final String openmrsUrl;

	private final static String ENCOUNTER_PATH = "/ws/rest/v1/encounter";
	private final static String ENCOUNTER_BY_PATIENT_UUID_PATH = ENCOUNTER_PATH + "?patient={uuid}&v=full";
	private final static String CONCEPT_PATH = "/ws/rest/v1/concept?q={conceptName}";
	private final static String PROVIDER_PATH = "/ws/rest/v1/person/{uuid}?v=full";
	private final static String CREATOR_PATH = "/ws/rest/v1/user/{uuid}?v=full";
	
	private UriTemplate encounterByPatientUuidTemplate;
	private UriTemplate conceptSearchByNameTemplate;
	private UriTemplate providerByUuidTemplate;
	private UriTemplate creatorByUuidTemplate;
	private URI encounterPathUri;
	
	@Autowired
	public OpenMrsEncounterUrlHolder(@Value("${openmrs.url}") String openmrsUrl) {
		this.openmrsUrl = openmrsUrl;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		encounterByPatientUuidTemplate = new UriTemplate(openmrsUrl + ENCOUNTER_BY_PATIENT_UUID_PATH);
		encounterPathUri = new URI(openmrsUrl + ENCOUNTER_PATH);
		conceptSearchByNameTemplate = new UriTemplate(openmrsUrl + CONCEPT_PATH);
		providerByUuidTemplate = new UriTemplate(openmrsUrl + PROVIDER_PATH);
		creatorByUuidTemplate = new UriTemplate(openmrsUrl + CREATOR_PATH);
	}
	
	public URI getEncountersByPatientUuid(String uuid) {
		return encounterByPatientUuidTemplate.expand(uuid);
	}
	
	public URI getEncounterPath() {
		return encounterPathUri;
	}
	
	public URI getConceptSearchByName(String name) {
		return conceptSearchByNameTemplate.expand(name);
	}
	
	public URI getProviderByUuid(String uuid) {
		return providerByUuidTemplate.expand(uuid);
	}
	
	public URI getCreatorByUuid(String uuid) {
		return creatorByUuidTemplate.expand(uuid);
	}
}

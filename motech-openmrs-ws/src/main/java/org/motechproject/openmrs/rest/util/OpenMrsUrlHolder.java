package org.motechproject.openmrs.rest.util;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;

@Component
public class OpenMrsUrlHolder implements InitializingBean {
	@Value("${openmrs.url}")
	private String openmrsUrl;
	
	@Value("${openmrs.rest.resource.patient}")
	private String PATIENT_PATH;
	
	@Value("${openmrs.rest.resource.patient.search}")
	private String PATIENT_SEARCH_PATH;
	
	@Value("${openmrs.rest.resource.patient.element.full}")
	private String PATIENT_FULL_BY_UUID_PATH;
	
	@Value("${openmrs.rest.resource.personidentifiertype.element.full}")
	private String PATIENT_IDENTIFIER_TYPE_LIST_PATH;
	
	@Value("${openmrs.rest.resource.person}")
	private String PERSON_PATH;
	
	@Value("${openmrs.rest.resource.person.element}")
	private String PERSON_UPDATE_PATH;
	
	@Value("${openmrs.rest.resource.person.element.attribute}")
	private String PERSON_ATTRIBUTE_PATH;
	
	@Value("${openmrs.rest.resource.personattribute.search}")
	private String PERSON_ATTRIBUTE_TYPE;
	
	@Value("${openmrs.rest.resource.location}")
	private String FACILITY_PATH;
	
	@Value("${openmrs.rest.resource.location.full}")
	private String FACILITY_LIST_ALL_PATH;
	
	@Value("${openmrs.rest.resource.location.full.search}")
	private String FACILITY_LIST_ALL_BY_NAME_PATH;
	
	@Value("${openmrs.rest.resource.location.element}")
	private String FACILITY_FIND_BY_UUID_PATH;
	
	@Value("${openmrs.rest.resource.encounter}")
	private String ENCOUNTER_PATH;
	
	@Value("${openmrs.rest.resource.encounter.search.full}")
	private String ENCOUNTER_BY_PATIENT_UUID_PATH;
	
	@Value("${openmrs.rest.resource.concept.search}")
	private final static String CONCEPT_PATH = "/ws/rest/v1/concept?q={conceptName}";
	
	private URI patient;
	private URI patientIdentifierTypeList;
	private UriTemplate patientSearchPathTemplate;
	private UriTemplate patientFullByUuidTemplate;

	private URI person;
	private UriTemplate personAttributeAdd;
	private UriTemplate personUpdateTemplate;
	private UriTemplate personAttributeType;
	
	private URI facilityListUri;
	private URI facilityCreateUri;
	private UriTemplate facilityListUriTemplate;
	private UriTemplate facilityFindUriTemplate;
	
	private UriTemplate encounterByPatientUuidTemplate;
	private UriTemplate conceptSearchByNameTemplate;
	private UriTemplate providerByUuidTemplate;
	private UriTemplate creatorByUuidTemplate;
	private URI encounterPathUri;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		createPatientUris();
		createPersonUris();
		createFacilityUris();
		createEncounterUris();
		createConceptUris();
	}

	private void createPatientUris() throws URISyntaxException {
	    patient = new URI(openmrsUrl + PATIENT_PATH);
		patientIdentifierTypeList = new URI(openmrsUrl + PATIENT_IDENTIFIER_TYPE_LIST_PATH);
		patientSearchPathTemplate = new UriTemplate(openmrsUrl + PATIENT_SEARCH_PATH);
		patientFullByUuidTemplate = new UriTemplate(openmrsUrl + PATIENT_FULL_BY_UUID_PATH);
    }

	private void createPersonUris() throws URISyntaxException {
	    person = new URI(openmrsUrl + PERSON_PATH);
		personAttributeAdd = new UriTemplate(openmrsUrl + PERSON_ATTRIBUTE_PATH);
		personAttributeType = new UriTemplate(openmrsUrl + PERSON_ATTRIBUTE_TYPE);
		personUpdateTemplate = new UriTemplate(openmrsUrl + PERSON_UPDATE_PATH);
    }
	
	private void createFacilityUris() throws URISyntaxException {
		facilityListUriTemplate = new UriTemplate(openmrsUrl + FACILITY_LIST_ALL_BY_NAME_PATH);
		facilityFindUriTemplate = new UriTemplate(openmrsUrl + FACILITY_FIND_BY_UUID_PATH);		
		facilityListUri = new URI(openmrsUrl + FACILITY_LIST_ALL_PATH);
		facilityCreateUri = new URI(openmrsUrl + FACILITY_PATH);
    }
	
	private void createEncounterUris() throws URISyntaxException {
		encounterByPatientUuidTemplate = new UriTemplate(openmrsUrl + ENCOUNTER_BY_PATIENT_UUID_PATH);
		encounterPathUri = new URI(openmrsUrl + ENCOUNTER_PATH);
	}
	
	private void createConceptUris() {
		conceptSearchByNameTemplate = new UriTemplate(openmrsUrl + CONCEPT_PATH);
	}

	public URI getPatient() {
		return patient;
	}
	
	public URI getPatientIdentifierTypeList() {
		return patientIdentifierTypeList;
	}
	
	public URI getSearchPathWithTerm(String term) {
		return patientSearchPathTemplate.expand(term);
	}
	
	public URI getFullPatientByUuid(String uuid) {
		return patientFullByUuidTemplate.expand(uuid);
	}
	
	public URI getPersonAttributeType(String attributeName) {
		return personAttributeType.expand(attributeName);
	}
	
	public URI getPersonAttributeAdd(String uuid) {
		return personAttributeAdd.expand(uuid);
	}

	public URI getPerson() {
		return person;
	}
	
	public URI getPersonUpdateByUuid(String uuid) {
		return personUpdateTemplate.expand(uuid);
	}
	
	public URI getFacilityListUri() {
		return facilityListUri;
	}

	public URI getFacilityCreateUri() {
		return facilityCreateUri;
	}

	public URI getFacilityListUri(String facilityName) {
		return facilityListUriTemplate.expand(facilityName);
	}

	public URI getFacilityFindUri(String facilityUuid) {
		return facilityFindUriTemplate.expand(facilityUuid);
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

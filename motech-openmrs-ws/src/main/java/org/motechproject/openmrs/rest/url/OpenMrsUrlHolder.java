package org.motechproject.openmrs.rest.url;

import java.net.URI;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;

@Component
public class OpenMrsUrlHolder implements InitializingBean {
	private final static String OPENMRS_REST_PATH = "/ws/rest/v1";
	
	private final static String PATIENT_PATH = OPENMRS_REST_PATH + "/patient";
	private final static String PATIENT_SEARCH_PATH = PATIENT_PATH + "?q={id}";
	private final static String PATIENT_FULL_BY_UUID_PATH = PATIENT_PATH + "/{uuid}?v=full";
	
	private final static String PATIENT_IDENTIFIER_TYPE_LIST_PATH = OPENMRS_REST_PATH + "/patientidentifiertype?v=full";
	
	private final static String PERSON_PATH = OPENMRS_REST_PATH + "/person";
	private final static String PERSON_UPDATE_PATH = PERSON_PATH + "/{uuid}";
	private final static String PERSON_ATTRIBUTE_PATH = PERSON_PATH + "/{uuid}/attribute";
	
	private final static String PERSON_ATTRIBUTE_TYPE = OPENMRS_REST_PATH + "/personattributetype?q={name}";
	
	private String openmrsUrl;
	
	private URI patient;
	private URI patientIdentifierTypeList;

	private UriTemplate patientAttributeAdd;
	private UriTemplate patientSearchPathTemplate;
	private UriTemplate patientFullByUuidTemplate;

	private URI person;
	private UriTemplate personUpdateTemplate;
	private UriTemplate personAttributeType;

	@Autowired
	public OpenMrsUrlHolder(@Value("${openmrs.url}")String openmrsUrl) {
		this.openmrsUrl = openmrsUrl;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		patient = new URI(openmrsUrl + PATIENT_PATH);
		person = new URI(openmrsUrl + PERSON_PATH);
		patientIdentifierTypeList = new URI(openmrsUrl + PATIENT_IDENTIFIER_TYPE_LIST_PATH);
		patientAttributeAdd = new UriTemplate(openmrsUrl + PERSON_ATTRIBUTE_PATH);
		personAttributeType = new UriTemplate(openmrsUrl + PERSON_ATTRIBUTE_TYPE);
		patientSearchPathTemplate = new UriTemplate(openmrsUrl + PATIENT_SEARCH_PATH);
		patientFullByUuidTemplate = new UriTemplate(openmrsUrl + PATIENT_FULL_BY_UUID_PATH);
		personUpdateTemplate = new UriTemplate(openmrsUrl + PERSON_UPDATE_PATH);
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
		return patientAttributeAdd.expand(uuid);
	}

	public URI getPerson() {
		return person;
	}
	
	public URI getPersonUpdateByUuid(String uuid) {
		return personUpdateTemplate.expand(uuid);
	}
}

package org.motechproject.openmrs.rest.url;

import java.net.URI;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;

@Component
public class OpenMrsPatientUrlHolder implements InitializingBean {
	
	private String openmrsUrl;
	
	private final static String PATIENT_PATH = "/ws/rest/v1/patient";
	private final static String PATIENT_SEARCH_PATH = PATIENT_PATH + "?q={id}";
	private final static String PATIENT_FULL_BY_UUID_PATH = PATIENT_PATH + "/{uuid}?v=full";
	private final static String PATIENT_IDENTIFIER_TYPE_LIST_PATH = "/ws/rest/v1/patientidentifiertype?v=full";
	private final static String PATIENT_ATTRIBUTE_ADD_PATH = "/ws/rest/v1/person/{uuid}/attributes";
	private final static String PATIENT_ATTRIBUTE_TYPE = "/ws/rest/v1/personattributetype?q={name}";
	private final static String PATIENT_UPDATE_PATH = PATIENT_PATH + "/{uuid}";
	
	private URI patient;
	private URI patientIdentifierTypeList;
	private UriTemplate patientAttributeAdd;
	private UriTemplate personAttributeType;
	private UriTemplate patientSearchPathTemplate;
	private UriTemplate patientFullByUuidTemplate;
	private UriTemplate patientUpdatePathTemplate;

	@Autowired
	public OpenMrsPatientUrlHolder(@Value("${openmrs.url}")String openmrsUrl) {
		this.openmrsUrl = openmrsUrl;
	}
	
	
	@Override
	public void afterPropertiesSet() throws Exception {
		patient = new URI(openmrsUrl + PATIENT_PATH);
		patientIdentifierTypeList = new URI(openmrsUrl + PATIENT_IDENTIFIER_TYPE_LIST_PATH);
		patientAttributeAdd = new UriTemplate(openmrsUrl + PATIENT_ATTRIBUTE_ADD_PATH);
		personAttributeType = new UriTemplate(openmrsUrl + PATIENT_ATTRIBUTE_TYPE);
		patientSearchPathTemplate = new UriTemplate(openmrsUrl + PATIENT_SEARCH_PATH);
		patientFullByUuidTemplate = new UriTemplate(openmrsUrl + PATIENT_FULL_BY_UUID_PATH);
		patientUpdatePathTemplate = new UriTemplate(openmrsUrl + PATIENT_UPDATE_PATH);
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

	public URI getPatientUpdatePathByUuid(String uuid) {
		return patientUpdatePathTemplate.expand(uuid);
	}	
}

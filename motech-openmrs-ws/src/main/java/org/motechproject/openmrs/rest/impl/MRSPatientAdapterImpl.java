package org.motechproject.openmrs.rest.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.services.MRSException;
import org.motechproject.mrs.services.MRSFacilityAdapter;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.openmrs.rest.HttpException;
import org.motechproject.openmrs.rest.RestfulClient;
import org.motechproject.openmrs.rest.util.JsonConverterUtil;
import org.motechproject.openmrs.rest.util.OpenMrsUrlHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MRSPatientAdapterImpl implements MRSPatientAdapter {

	private static Logger logger = LoggerFactory.getLogger(MRSPatientAdapterImpl.class);

	private String motechIdTypeUuid;

	private final RestfulClient restfulClient;
	private final MRSFacilityAdapter facilityAdapter;
	private final OpenMrsUrlHolder urlHolder;
	private final MRSPersonAdapterImpl personAdapter;

	private final static String MOTECH_ID_NAME = "MoTeCH Id";

	@Autowired
	public MRSPatientAdapterImpl(RestfulClient restfulClient, MRSFacilityAdapter facilityAdapter,
	        OpenMrsUrlHolder patientUrls, MRSPersonAdapterImpl personAdapter) {
		this.restfulClient = restfulClient;
		this.facilityAdapter = facilityAdapter;
		this.urlHolder = patientUrls;
		this.personAdapter = personAdapter;
	}

	@Override
	public Integer getAgeOfPatientByMotechId(String arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public MRSPatient getPatientByMotechId(String motechId) {
		Validate.notEmpty(motechId, "Motech Id cannot be empty");

		JsonNode resultObj = null;
		try {
			resultObj = restfulClient.getEntityByJsonNode(urlHolder.getSearchPathWithTerm(motechId));
		} catch (HttpException e) {
			logger.error("Failed search for patient by MoTeCH Id: " + motechId);
			throw new MRSException(e);
		}

		JsonNode resultArray = resultObj.get("results");

		if (resultArray.size() == 0) {
			logger.debug("No search results found");
			return null;
		} else if (resultArray.size() > 1) {
			logger.warn("Search for patient by id returned more than 1 result");
		}

		JsonNode resultObj1 = resultArray.get(0);
		String uuid = resultObj1.get("uuid").getValueAsText();

		return getPatient(uuid);
	}

	@Override
	public MRSPatient getPatient(String patientId) {
		Validate.notEmpty(patientId, "Patient Id cannot be empty");

		JsonNode responseNode = null;
		try {
			responseNode = restfulClient.getEntityByJsonNode(urlHolder.getFullPatientByUuid(patientId));
		} catch (HttpException e) {
			logger.error("Failed to get patient by id: " + patientId);
			throw new MRSException(e);
		}

		JsonNode motechIdentifier = getMotechIdentifier(responseNode);
		MRSPerson person = JsonConverterUtil.convertJsonToMrsPerson(responseNode.get("person"));

		MRSFacility facility = facilityAdapter.getFacility(motechIdentifier.get("location").get("uuid").getValueAsText());

		MRSPatient patient = new MRSPatient(patientId, motechIdentifier.get("identifier").getValueAsText(), person, facility);

		return patient;
	}

	private JsonNode getMotechIdentifier(JsonNode responseNode) {
		if (StringUtils.isEmpty(motechIdTypeUuid)) {
			getMotechIdUuid();
		}

		JsonNode identifiers = responseNode.get("identifiers");

		for (int i = 0; i < identifiers.size(); i++) {
			JsonNode identifier = identifiers.get(i);
			JsonNode identifierType = identifier.get("identifierType");
			if (motechIdTypeUuid.equals(identifierType.get("uuid").getValueAsText())) {
				return identifier;
			}
		}

		logger.error("Could not resolve the uuid of the MoTeCH Patient Identifer type");
		throw new MRSException(new RuntimeException("Could not find the MoTeCH Patient Identifer type"));
	}

	private String getMotechIdUuid() {
		if (StringUtils.isNotEmpty(motechIdTypeUuid)) {
			return motechIdTypeUuid;
		}

		JsonNode results = null;
		try {
			results = restfulClient.getEntityByJsonNode(urlHolder.getPatientIdentifierTypeList());
			results = results.get("results");
		} catch (HttpException e) {
			logger.error("There was an exception retrieving the MoTeCH Identifier Type UUID");
			throw new MRSException(e);
		}

		for (int i = 0; i < results.size(); i++) {
			JsonNode obj = results.get(i);
			if (MOTECH_ID_NAME.equals(obj.get("name").getValueAsText())) {
				motechIdTypeUuid = obj.get("uuid").getValueAsText();
				break;
			}
		}

		if (StringUtils.isEmpty(motechIdTypeUuid)) {
			logger.error("Could not find MoTeCH Identifier Type in OpenMRS");
			throw new MRSException(new RuntimeException("Could not find MoTeCH Identifier type with name: "
			        + MOTECH_ID_NAME));
		}

		return motechIdTypeUuid;
	}

	@Override
	public MRSPatient savePatient(MRSPatient patient) {
		Validate.notNull(patient, "Patient cannot be null");
		Validate.isTrue(patient.getId() != null || patient.getMotechId() != null,
		        "You must provide a patient id or motech id to save a patient");
		
		MRSPerson savedPerson = personAdapter.savePerson(patient.getPerson());
		
		JsonNode patientJsonObj = makeJsonPatientObjFromMrsPatient(patient, savedPerson.getId());
		JsonNode response = null;
		try {
			response = restfulClient.postForJsonNode(urlHolder.getPatient(), patientJsonObj);
		} catch (HttpException e) {
			logger.error("Failed to create a patient in OpenMRS with MoTeCH Id: " + patient.getMotechId());
			throw new MRSException(e);
		}

		return new MRSPatient(response.get("uuid").getValueAsText(), patient.getMotechId(), savedPerson,
		        patient.getFacility());
	}

	private JsonNode makeJsonPatientObjFromMrsPatient(MRSPatient patient, String personId) {
		ObjectNode patientObj = JsonNodeFactory.instance.objectNode();

		patientObj.put("identifiers", buildPreferredIdentiferObj(patient));
		patientObj.put("person", personId);

		return patientObj;
	}

	private ArrayNode buildPreferredIdentiferObj(MRSPatient patient) {
		ArrayNode identifiersArray = JsonNodeFactory.instance.arrayNode();
		ObjectNode preferredIdentifier = JsonNodeFactory.instance.objectNode();
		preferredIdentifier.put("identifier", patient.getMotechId());
		preferredIdentifier.put("identifierType", getMotechIdUuid());
		preferredIdentifier.put("location", patient.getFacility().getId());

		identifiersArray.add(preferredIdentifier);
		return identifiersArray;
	}

	@Override
	public void savePatientCauseOfDeathObservation(String patientId, String conceptName, Date dateOfDeath,
	        String comment) {
		Validate.notEmpty(patientId, "Patient id cannot be empty");
		
		personAdapter.savePersonCauseOfDeath(patientId, dateOfDeath, conceptName);
	}

	@Override
	public List<MRSPatient> search(String name, String id) {
		Validate.notEmpty(name, "Name cannot be empty");
		
		JsonNode resultObj = null;
		try {
			resultObj = restfulClient.getEntityByJsonNode(urlHolder.getSearchPathWithTerm(name));
		} catch (HttpException e) {
			logger.error("Failed search for patient with name: " + name + ", and id: " + id);
			throw new MRSException(e);
		}

		List<MRSPatient> searchResults = new ArrayList<MRSPatient>();
		JsonNode resultArray = resultObj.get("results");
		for (int i = 0; i < resultArray.size(); i++) {
			MRSPatient patient = getPatient(resultArray.get(i).get("uuid").getValueAsText());
			if (id == null) {
				searchResults.add(patient);
			} else {
				if (patient.getMotechId() != null && patient.getMotechId().contains(id)) {
					searchResults.add(patient);
				}
			}
		}

		if (searchResults.size() > 0) {
			sortResults(searchResults);
		}

		return searchResults;
	}

	private void sortResults(List<MRSPatient> searchResults) {
		Collections.sort(searchResults, new Comparator<MRSPatient>() {
			@Override
			public int compare(MRSPatient patient1, MRSPatient patient2) {
				if (StringUtils.isNotEmpty(patient1.getMotechId()) && StringUtils.isNotEmpty(patient2.getMotechId())) {
					return patient1.getMotechId().compareTo(patient2.getMotechId());
				} else if (StringUtils.isNotEmpty(patient1.getMotechId())) {
					return -1;
				} else if (StringUtils.isNotEmpty(patient2.getMotechId())) {
					return 1;
				}
				return 0;
			}
		});
	}

	@Override
	public String updatePatient(MRSPatient patient) {
		Validate.notNull(patient, "Patient cannot be null");
		Validate.notEmpty(patient.getId(), "Patient Id may not be empty");
		
		MRSPerson person = patient.getPerson();
		
		personAdapter.updatePerson(person);
		// the openmrs web service requires an explicit delete request to remove
		// attributes. delete all previous attributes, and then
		// create any attributes attached to the patient
		personAdapter.deleteAllAttributes(person);
		personAdapter.saveAttributesForPerson(person, person.getId());

		return patient.getMotechId();
	}
}

package org.motechproject.openmrs.rest.impl;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.services.MRSException;
import org.motechproject.mrs.services.MRSFacilityAdapter;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.openmrs.rest.HttpException;
import org.motechproject.openmrs.rest.RestfulClient;
import org.motechproject.openmrs.rest.util.DateUtil;
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
	private Map<String, String> attributeTypeUuidCache = new HashMap<String, String>();

	private final static String MOTECH_ID_NAME = "MoTeCH Id";

	@Autowired
	public MRSPatientAdapterImpl(RestfulClient restfulClient, MRSFacilityAdapter facilityAdapter,
	        OpenMrsUrlHolder patientUrls) {
		this.restfulClient = restfulClient;
		this.facilityAdapter = facilityAdapter;
		this.urlHolder = patientUrls;
	}

	@Override
	public Integer getAgeOfPatientByMotechId(String arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public MRSPatient getPatientByMotechId(String motechId) {
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
		String uuid = resultObj1.get("uuid").asText();

		return getPatient(uuid);
	}

	@Override
	public MRSPatient getPatient(String patientId) {
		JsonNode responseNode = null;
		try {
			responseNode = restfulClient.getEntityByJsonNode(urlHolder.getFullPatientByUuid(patientId));
		} catch (HttpException e) {
			logger.error("Failed to get patient by id: " + patientId);
			throw new MRSException(e);
		}

		JsonNode motechIdentifier = getMotechIdentifier(responseNode);
		MRSPerson person = JsonConverterUtil.convertJsonToMrsPerson(responseNode.get("person"));

		MRSFacility facility = facilityAdapter.getFacility(motechIdentifier.get("location").get("uuid").asText());

		MRSPatient patient = new MRSPatient(patientId, motechIdentifier.get("identifier").asText(), person, facility);

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
			if (motechIdTypeUuid.equals(identifierType.get("uuid").asText())) {
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
			if (MOTECH_ID_NAME.equals(obj.get("name").asText())) {
				motechIdTypeUuid = obj.get("uuid").asText();
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
		JsonNode personJsonObj = makeJsonPersonObjFromMrsPatient(patient, true);
		try {
			JsonNode response = restfulClient.postForJsonNode(urlHolder.getPerson(), personJsonObj);
			patient.getPerson().id(response.get("uuid").asText());
		} catch (HttpException e) {
			logger.error("Failed to create person for patient: " + patient.getMotechId());
			throw new MRSException(e);
		}

		saveAttributesForPerson(patient.getPerson(), patient.getPerson().getId());

		JsonNode patientJsonObj = makeJsonPatientObjFromMrsPatient(patient);
		JsonNode response = null;
		try {
			response = restfulClient.postForJsonNode(urlHolder.getPatient(), patientJsonObj);
		} catch (HttpException e) {
			logger.error("Failed to create a patient in OpenMRS with MoTeCH Id: " + patient.getMotechId());
			throw new MRSException(e);
		}

		return new MRSPatient(response.get("uuid").asText(), patient.getMotechId(), patient.getPerson(),
		        patient.getFacility());
	}

	private ObjectNode makeJsonPersonObjFromMrsPatient(MRSPatient patient, boolean creating) {
		MRSPerson person = patient.getPerson();

		ObjectNode personObj = buildPersonObjFromPerson(person);
		if (creating) {
			personObj.put("names", buildNamesForPerson(person, true));
			personObj.put("addresses", buildaddressesForPerson(person, true));
		} else {
			personObj.put("preferredName", buildNamesForPerson(person, false));
			personObj.put("preferredAddress", buildaddressesForPerson(person, false));
		}

		return personObj;
	}

	private ObjectNode buildPersonObjFromPerson(MRSPerson person) {
		ObjectNode patientObj = JsonNodeFactory.instance.objectNode();
		patientObj.put("birthdate", DateUtil.formatToOpenMrsDate(person.getDateOfBirth()));
		patientObj.put("gender", person.getGender());

		// guard against birthDateEstimated being null
		boolean dobEstimated = BooleanUtils.isTrue(person.getBirthDateEstimated()) ? true : false;
		patientObj.put("birthdateEstimated", dobEstimated);
		patientObj.put("dead", person.isDead());

		if (person.deathDate() != null) {
			patientObj.put("deathDate", DateUtil.formatToOpenMrsDate(person.deathDate()));
		}

		return patientObj;
	}

	private JsonNode buildNamesForPerson(MRSPerson person, boolean withArray) {
		ObjectNode preferredName = JsonNodeFactory.instance.objectNode();
		preferredName.put("givenName", person.getFirstName());
		preferredName.put("middleName", person.getMiddleName());
		preferredName.put("familyName", person.getLastName());

		if (withArray) {
			ArrayNode namesArray = JsonNodeFactory.instance.arrayNode();
			namesArray.add(preferredName);
			return namesArray;
		} else {
			return preferredName;
		}
	}

	private JsonNode buildaddressesForPerson(MRSPerson person, boolean withArray) {
		ObjectNode preferredAddress = JsonNodeFactory.instance.objectNode();
		preferredAddress.put("address1", person.getAddress());

		if (withArray) {
			ArrayNode addressArray = JsonNodeFactory.instance.arrayNode();
			addressArray.add(preferredAddress);
			return addressArray;
		} else {
			return preferredAddress;
		}
	}

	private JsonNode makeJsonPatientObjFromMrsPatient(MRSPatient patient) {
		ObjectNode patientObj = JsonNodeFactory.instance.objectNode();

		patientObj.put("identifiers", buildPreferredIdentiferObj(patient));
		patientObj.put("person", patient.getPerson().getId());

		return patientObj;
	}

	private void saveAttributesForPerson(MRSPerson person, String persistedPersonUuid) {
		for (Attribute attribute : person.getAttributes()) {
			try {
				ObjectNode obj = JsonNodeFactory.instance.objectNode();
				String value = attribute.value();
				String name = URLEncoder.encode(attribute.name(), "UTF-8");
				obj.put("value", value);
				obj.put("attributeType", getAttributeTypeUuid(name));
				restfulClient.postForJsonNode(urlHolder.getPersonAttributeAdd(persistedPersonUuid), obj);
			} catch (HttpException e) {
				logger.warn("Unable to add attribute to person with id: " + person.getId());
			} catch (UnsupportedEncodingException e) {
				logger.warn("There was an error encoding the attribute name: " + attribute.name()
				        + " on person with id: " + person.getId());
			}
		}
	}

	private String getAttributeTypeUuid(String name) {
		if (!attributeTypeUuidCache.containsKey(name)) {
			try {
				JsonNode attributeType = restfulClient.getEntityByJsonNode(urlHolder.getPersonAttributeType(name));
				JsonNode resultArray = attributeType.get("results");
				if (resultArray.size() == 0) {
					logger.warn("No attribute found with name: " + name);
				}

				String attrUuid = resultArray.get(0).get("uuid").asText();
				attributeTypeUuidCache.put(name, attrUuid);
			} catch (HttpException e) {
			}
		}

		return attributeTypeUuidCache.get(name);
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
		ObjectNode obj = JsonNodeFactory.instance.objectNode();
		obj.put("dead", true);
		obj.put("deathDate", DateUtil.formatToOpenMrsDate(dateOfDeath));
		obj.put("causeOfDeath", conceptName);
		try {
			restfulClient.postWithEmptyResponseBody(urlHolder.getPersonByUuid(patientId), obj);
		} catch (HttpException e) {
			logger.error("Failed to save cause of death observation for patient id: " + patientId);
			throw new MRSException(e);
		}
	}

	@Override
	public List<MRSPatient> search(String name, String id) {
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
			MRSPatient patient = getPatient(resultArray.get(i).get("uuid").asText());
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
		if (StringUtils.isEmpty(patient.getId())) {
			throw new MRSException(new IllegalArgumentException("Patient must have an id to be updated"));
		}

		ObjectNode personJsonObj = makeJsonPersonObjFromMrsPatient(patient, false);
		try {
			// must update the name and address separately when updating a person
			// this requires finding the uuid's of the name/address elements
			JsonNode personResponse = restfulClient.getEntityByJsonNode(urlHolder.getPersonByUuid(patient.getId()));
			ObjectNode preferredName = (ObjectNode) personJsonObj.remove("preferredName");
			ObjectNode preferredAddress = (ObjectNode) personJsonObj.remove("preferredAddress");
			restfulClient.postWithEmptyResponseBody(
			        urlHolder.getPersonNameByUuid(patient.getId(), personResponse.get("preferredName").get("uuid")
			                .asText()), preferredName);
			restfulClient.postWithEmptyResponseBody(
			        urlHolder.getPersonAddressByUuid(patient.getId(),
			                personResponse.get("preferredAddress").get("uuid").asText()), preferredAddress);
			restfulClient.postWithEmptyResponseBody(urlHolder.getPersonByUuid(patient.getId()), personJsonObj);
		} catch (HttpException e) {
			logger.error("Failed to update a patient in OpenMRS with MoTeCH Id: " + patient.getMotechId());
			throw new MRSException(e);
		}

		// the openmrs web service requires an explicit delete request to remove
		// attributes. delete all previous attributes, and then
		// create any attributes attached to the patient
		deleteAllAttributes(patient);
		saveAttributesForPerson(patient.getPerson(), patient.getId());

		return patient.getMotechId();
	}

	private void deleteAllAttributes(MRSPatient patient) {
		JsonNode patientObj = null;
		try {
			patientObj = restfulClient.getEntityByJsonNode(urlHolder.getFullPatientByUuid(patient.getId()));
		} catch (HttpException e) {
			logger.warn("Could retrieve patient with id: " + patient.getId());
			return;
		}

		JsonNode attributesArray = patientObj.get("person").get("attributes");
		for (int i = 0; i < attributesArray.size(); i++) {
			JsonNode attribute = attributesArray.get(i);
			String attributeUri = attribute.get("links").get(0).get("uri").asText();
			try {
				restfulClient.deleteEntity(new URI(attributeUri));
			} catch (HttpException e) {
				logger.warn("Failed to delete attribute with uuid: " + attribute.get("uuid").asText());
			} catch (URISyntaxException e) {
				logger.warn("Error with patient attribute uri: " + attribute.get("uri").asText());
			}
		}
	}
}

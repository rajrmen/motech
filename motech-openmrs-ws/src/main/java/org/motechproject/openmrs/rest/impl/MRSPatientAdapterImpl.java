package org.motechproject.openmrs.rest.impl;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.ParseException;
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
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.services.MRSException;
import org.motechproject.mrs.services.MRSFacilityAdapter;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.openmrs.rest.DateUtil;
import org.motechproject.openmrs.rest.HttpException;
import org.motechproject.openmrs.rest.RestfulClient;
import org.motechproject.openmrs.rest.url.OpenMrsPatientUrlHolder;
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
	private final OpenMrsPatientUrlHolder patientUrls;
	private Map<String, String> attributeTypeUuidCache = new HashMap<String, String>();

	private final static String MOTECH_ID_NAME = "MoTeCH Id";

	@Autowired
	public MRSPatientAdapterImpl(RestfulClient restfulClient, MRSFacilityAdapter facilityAdapter,
	        OpenMrsPatientUrlHolder patientUrls) {
		this.restfulClient = restfulClient;
		this.facilityAdapter = facilityAdapter;
		this.patientUrls = patientUrls;
	}

	@Override
	public Integer getAgeOfPatientByMotechId(String arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public MRSPatient getPatientByMotechId(String motechId) {
		JsonNode resultObj = null;
		try {
			resultObj = restfulClient.getEntityByJsonNode(patientUrls.getSearchPathWithTerm(motechId));
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
			responseNode = restfulClient.getEntityByJsonNode(patientUrls.getFullPatientByUuid(patientId));
		} catch (HttpException e) {
			logger.error("Failed to get patient by id: " + patientId);
			throw new MRSException(e);
		}

		JsonNode motechIdentifier = getMotechIdentifier(responseNode);

		MRSPerson person = new MRSPerson();
		JsonNode personNode = responseNode.get("person");
		setPreferredName(person, personNode);
		setPersonProperties(person, personNode);
		setPreferredAddress(person, personNode);
		setAttributes(person, personNode);

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
			results = restfulClient.getEntityByJsonNode(patientUrls.getPatientIdentifierTypeList());
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

	private void setPreferredName(MRSPerson person, JsonNode personNode) {
		JsonNode preferredNameObj = personNode.get("preferredName");

		person.preferredName(preferredNameObj.get("display").asText())
		        .firstName(preferredNameObj.get("givenName").asText())
		        .middleName(preferredNameObj.get("middleName").asText())
		        .lastName(preferredNameObj.get("familyName").asText());
	}

	private void setPersonProperties(MRSPerson person, JsonNode personNode) {
		person.birthDateEstimated(personNode.get("birthdateEstimated").asBoolean())
		        .gender(personNode.get("gender").asText()).dead(personNode.get("dead").asBoolean());

		try {
			person.dateOfBirth(DateUtil.parseOpenMrsDate(personNode.get("birthdate").asText()));
		} catch (ParseException e) {
			logger.warn("Could not parse the birthdate property on Person with uuid: "
			        + personNode.get("uuid").asText());
		}

		if (!personNode.get("deathDate").isNull()) {
			try {
				person.deathDate(DateUtil.parseOpenMrsDate(personNode.get("deathDate").asText()));
			} catch (ParseException e) {
				logger.warn("Could not parse the deathDate property on Person with uuid: "
				        + personNode.get("uuid").asText());
			}
		}
	}

	private void setPreferredAddress(MRSPerson person, JsonNode personNode) {
		JsonNode preferredAddress = personNode.get("preferredAddress");
		person.address(preferredAddress.get("address1").asText());
	}

	private void setAttributes(MRSPerson person, JsonNode personNode) {
		JsonNode attributes = personNode.get("attributes");
		if (attributes.size() == 0) {
			return;
		}

		for (int i = 0; i < attributes.size(); i++) {
			// extract name/value from the display property
			// there is no explicit property for name attribute
			// the display attribute is formatted as: name = value
			String display = attributes.get(i).get("display").asText();
			int index = display.indexOf("=");
			String name = display.substring(0, index).trim();
			String value = display.substring(index + 1).trim();
			Attribute attr = new Attribute(name, value);
			person.addAttribute(attr);
		}
	}

	@Override
	public MRSPatient savePatient(MRSPatient patient) {
		JsonNode patientJsonObj = makeJsonPatientObjFromMrsPatient(patient);
		JsonNode response = null;
		try {
			response = restfulClient.postForJsonNode(patientUrls.getPatient(), patientJsonObj);
		} catch (HttpException e) {
			logger.error("Failed to create a patient in OpenMRS with MoTeCH Id: " + patient.getMotechId());
			throw new MRSException(e);
		}

		String persistedPatientUuid = response.get("uuid").asText();
		saveAttributesForPerson(patient.getPerson(), persistedPatientUuid);

		patient.getPerson().id(persistedPatientUuid);
		return new MRSPatient(persistedPatientUuid, patient.getMotechId(), patient.getPerson(), patient.getFacility());
	}

	private JsonNode makeJsonPatientObjFromMrsPatient(MRSPatient patient) {
		MRSPerson person = patient.getPerson();

		ObjectNode patientObj = buildPatientObjFromPerson(person);
		ObjectNode preferredIdentifier = buildPreferredIdentiferObj(patient);
		ObjectNode preferredName = buildPreferredNameObj(person);
		ObjectNode preferredAddress = buildPreferredAddressObj(person);

		patientObj.put("preferredIdentifier", preferredIdentifier);
		patientObj.put("preferredName", preferredName);
		patientObj.put("preferredAddress", preferredAddress);

		return patientObj;
	}

	private ObjectNode buildPatientObjFromPerson(MRSPerson person) {
		ObjectNode patientObj = JsonNodeFactory.instance.objectNode();
		patientObj.put("birthdate", DateUtil.formatToOpenMrsDate(person.getDateOfBirth()));
		patientObj.put("gender", person.getGender());

		// guard against birthDateEstimated being null
		boolean dobEstimated = BooleanUtils.isTrue(person.getBirthDateEstimated()) ? true : false;
		patientObj.put("birthdateEstimated", dobEstimated);
		patientObj.put("dead", person.isDead());
		patientObj.put("deathDate",
		        (person.deathDate() == null ? null : DateUtil.formatToOpenMrsDate(person.deathDate())));
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
				restfulClient.postForJsonNode(patientUrls.getPersonAttributeAdd(persistedPersonUuid), obj);
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
				JsonNode attributeType = restfulClient.getEntityByJsonNode(patientUrls.getPersonAttributeType(name));
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

	private ObjectNode buildPreferredIdentiferObj(MRSPatient patient) {
		ObjectNode preferredIdentifier = JsonNodeFactory.instance.objectNode();
		preferredIdentifier.put("identifier", patient.getMotechId());
		preferredIdentifier.put("identifierType", getMotechIdUuid());
		preferredIdentifier.put("location", patient.getFacility().getId());
		return preferredIdentifier;
	}

	private ObjectNode buildPreferredNameObj(MRSPerson person) {
		ObjectNode preferredName = JsonNodeFactory.instance.objectNode();
		preferredName.put("givenName", person.getFirstName());
		preferredName.put("middleName", person.getMiddleName());
		preferredName.put("familyName", person.getLastName());
		return preferredName;
	}

	private ObjectNode buildPreferredAddressObj(MRSPerson person) {
		ObjectNode preferredAddress = JsonNodeFactory.instance.objectNode();
		preferredAddress.put("address1", person.getAddress());

		return preferredAddress;
	}

	@Override
	public void savePatientCauseOfDeathObservation(String patientId, String conceptName, Date dateOfDeath,
	        String comment) {
		ObjectNode obj = JsonNodeFactory.instance.objectNode();
		obj.put("dead", true);
		obj.put("deathDate", DateUtil.formatToOpenMrsDate(dateOfDeath));
		obj.put("causeOfDeath", conceptName);
		try {
			restfulClient.postWithEmptyResponseBody(patientUrls.getPatientUpdatePathByUuid(patientId), obj);
		} catch (HttpException e) {
			logger.error("Failed to save cause of death observation for patient id: " + patientId);
			throw new MRSException(e);
		}
	}

	@Override
	public List<MRSPatient> search(String name, String id) {
		JsonNode resultObj = null;
		try {
			resultObj = restfulClient.getEntityByJsonNode(patientUrls.getSearchPathWithTerm(name));
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

		JsonNode patientJsonObj = makeJsonPatientObjFromMrsPatient(patient);
		try {
			restfulClient.postWithEmptyResponseBody(patientUrls.getPatientUpdatePathByUuid(patient.getId()),
			        patientJsonObj);
		} catch (HttpException e) {
			logger.error("Failed to update a patient in OpenMRS with MoTeCH Id: " + patient.getMotechId());
			throw new MRSException(e);
		}

		// the openmrs web service requires an explicit delete request to remove
		// attributes. Since there is no way of telling which attributes are new
		// or which have been removed, delete all previous attributes, and then
		// create any attributes attached to the patient
		deleteAllAttributes(patient);
		saveAttributesForPerson(patient.getPerson(), patient.getId());

		return patient.getMotechId();
	}

	private void deleteAllAttributes(MRSPatient patient) {
		JsonNode patientObj = null;
		try {
			patientObj = restfulClient.getEntityByJsonNode(patientUrls.getFullPatientByUuid(patient.getId()));
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

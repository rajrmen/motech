package org.motechproject.openmrs.rest.impl;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.services.MRSException;
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
public class MRSPersonAdapterImpl {
	private static final Logger logger = LoggerFactory.getLogger(MRSPersonAdapterImpl.class);
	
	private Map<String, String> attributeTypeUuidCache = new HashMap<String, String>();
	
	private RestfulClient restfulClient;
	private OpenMrsUrlHolder urlHolder;

	@Autowired
	public MRSPersonAdapterImpl(RestfulClient restfulClient, OpenMrsUrlHolder urlHolder) {
		this.restfulClient = restfulClient;
		this.urlHolder = urlHolder;
	}
	
	MRSPerson getPerson(String uuid) {
		try {
			JsonNode providerObj =  restfulClient.getEntityByJsonNode(urlHolder.getPersonFullByUuid(uuid));
			 return JsonConverterUtil.convertJsonToMrsPerson(providerObj);
		} catch (HttpException e) {
			throw new MRSException(e);
		}
	}

	MRSPerson savePerson(MRSPerson person) {
		JsonNode personJsonObj = JsonConverterUtil.makeJsonPersonObjFromMrsPerson(person, true);
		try {
			JsonNode response = restfulClient.postForJsonNode(urlHolder.getPerson(), personJsonObj);
			person.id(response.get("uuid").asText());
		} catch (HttpException e) {
			logger.error("Failed to create person for patient: " + person.getFullName());
			throw new MRSException(e);
		}

		saveAttributesForPerson(person, person.getId());		
		
		return person;
	}
	
	void saveAttributesForPerson(MRSPerson person, String persistedPersonUuid) {
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
	
	void deleteAllAttributes(MRSPerson person) {
		JsonNode patientObj = null;
		try {
			patientObj = restfulClient.getEntityByJsonNode(urlHolder.getFullPatientByUuid(person.getId()));
		} catch (HttpException e) {
			logger.warn("Could retrieve patient with id: " + person.getId());
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

	void updatePerson(MRSPerson person) {
		ObjectNode personJsonObj = JsonConverterUtil.makeJsonPersonObjFromMrsPerson(person, false);
		try {
			// must update the name and address separately when updating a
			// person
			// this requires finding the uuid's of the name/address elements
			JsonNode personResponse = restfulClient.getEntityByJsonNode(urlHolder.getPersonByUuid(person.getId()));
			ObjectNode preferredName = (ObjectNode) personJsonObj.remove("preferredName");
			ObjectNode preferredAddress = (ObjectNode) personJsonObj.remove("preferredAddress");
			restfulClient.postWithEmptyResponseBody(
			        urlHolder.getPersonNameByUuid(person.getId(), personResponse.get("preferredName").get("uuid")
			                .asText()), preferredName);
			restfulClient.postWithEmptyResponseBody(
			        urlHolder.getPersonAddressByUuid(person.getId(), personResponse.get("preferredAddress")
			                .get("uuid").asText()), preferredAddress);
			restfulClient.postWithEmptyResponseBody(urlHolder.getPersonByUuid(person.getId()), personJsonObj);
		} catch (HttpException e) {
			logger.error("Failed to update a person in OpenMRS with id: " + person.getId());
			throw new MRSException(e);
		}
    }
	
	void savePersonCauseOfDeath(String patientId, Date dateOfDeath, String conceptName) {
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
}

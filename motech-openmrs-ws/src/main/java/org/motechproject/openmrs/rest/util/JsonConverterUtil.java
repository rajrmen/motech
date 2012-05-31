package org.motechproject.openmrs.rest.util;

import java.text.ParseException;

import org.apache.commons.lang.BooleanUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonConverterUtil {

	private static Logger logger = LoggerFactory.getLogger(JsonConverterUtil.class);

	public static MRSFacility convertJsonToMrsFacility(JsonNode facilityObj) {
		MRSFacility facility = new MRSFacility(facilityObj.get("uuid").getValueAsText(), facilityObj.get("name").getValueAsText(),
				facilityObj.get("country").getValueAsText(), facilityObj.get("address6").getValueAsText(), facilityObj.get(
						"countyDistrict").getValueAsText(), facilityObj.get("stateProvince").getValueAsText());
		return facility;
	}

	public static MRSPerson convertJsonToMrsPerson(JsonNode personNode) {
		MRSPerson person = new MRSPerson();
		setPreferredName(person, personNode);
		setPersonProperties(person, personNode);
		setPreferredAddress(person, personNode);
		setAttributes(person, personNode);

		return person;
	}

	private static void setPreferredName(MRSPerson person, JsonNode personNode) {
		JsonNode preferredNameObj = personNode.get("preferredName");

		person.preferredName(preferredNameObj.get("display").getValueAsText());
		
		if (!preferredNameObj.get("givenName").isNull()) {
			person.firstName(preferredNameObj.get("givenName").getValueAsText());
		}
		
		if (!preferredNameObj.get("middleName").isNull()) {
			person.middleName(preferredNameObj.get("middleName").getValueAsText());
		}
		 
		if (!preferredNameObj.get("familyName").isNull()) {
			person.lastName(preferredNameObj.get("familyName").getValueAsText());
		}
	}

	private static void setPersonProperties(MRSPerson person, JsonNode personNode) {
		person.id(personNode.get("uuid").getValueAsText()).birthDateEstimated(personNode.get("birthdateEstimated").getValueAsBoolean())
				.gender(personNode.get("gender").getValueAsText()).dead(personNode.get("dead").getValueAsBoolean());

		try {
			person.dateOfBirth(DateUtil.parseOpenMrsDate(personNode.get("birthdate").getValueAsText()));
		} catch (ParseException e) {
			logger.warn("Could not parse the birthdate property on Person with uuid: "
					+ personNode.get("uuid").getValueAsText());
		}

		if (!personNode.get("deathDate").isNull()) {
			try {
				person.deathDate(DateUtil.parseOpenMrsDate(personNode.get("deathDate").getValueAsText()));
			} catch (ParseException e) {
				logger.warn("Could not parse the deathDate property on Person with uuid: "
						+ personNode.get("uuid").getValueAsText());
			}
		}
	}

	private static void setPreferredAddress(MRSPerson person, JsonNode personNode) {
		JsonNode preferredAddress = personNode.get("preferredAddress");
		if (!preferredAddress.isNull()) {
			person.address(preferredAddress.get("address1").getValueAsText());
		}
	}

	private static void setAttributes(MRSPerson person, JsonNode personNode) {
		JsonNode attributes = personNode.get("attributes");
		if (attributes.size() == 0) {
			return;
		}

		for (int i = 0; i < attributes.size(); i++) {
			// extract name/value from the display property
			// there is no explicit property for name attribute
			// the display attribute is formatted as: name = value
			String display = attributes.get(i).get("display").getValueAsText();
			int index = display.indexOf("=");
			String name = display.substring(0, index).trim();
			String value = display.substring(index + 1).trim();
			Attribute attr = new Attribute(name, value);
			person.addAttribute(attr);
		}
	}
	
	public static ObjectNode makeJsonPersonObjFromMrsPerson(MRSPerson person, boolean creating) {
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
	
	private static ObjectNode buildPersonObjFromPerson(MRSPerson person) {
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

	private static JsonNode buildNamesForPerson(MRSPerson person, boolean withArray) {
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

	private static JsonNode buildaddressesForPerson(MRSPerson person, boolean withArray) {
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
}

package org.motechproject.openmrs.rest.util;

import java.text.ParseException;

import org.codehaus.jackson.JsonNode;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPerson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonConverterUtil {

	private static Logger logger = LoggerFactory.getLogger(JsonConverterUtil.class);

	public static MRSFacility convertJsonToMrsFacility(JsonNode facilityObj) {
		MRSFacility facility = new MRSFacility(facilityObj.get("uuid").asText(), facilityObj.get("name").asText(),
				facilityObj.get("country").asText(), facilityObj.get("address6").asText(), facilityObj.get(
						"countyDistrict").asText(), facilityObj.get("stateProvince").asText());
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

		person.preferredName(preferredNameObj.get("display").asText());
		
		if (!preferredNameObj.get("givenName").isNull()) {
			person.firstName(preferredNameObj.get("givenName").asText());
		}
		
		if (!preferredNameObj.get("middleName").isNull()) {
			person.middleName(preferredNameObj.get("middleName").asText());
		}
		 
		if (!preferredNameObj.get("familyName").isNull()) {
			person.lastName(preferredNameObj.get("familyName").asText());
		}
	}

	private static void setPersonProperties(MRSPerson person, JsonNode personNode) {
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

	private static void setPreferredAddress(MRSPerson person, JsonNode personNode) {
		JsonNode preferredAddress = personNode.get("preferredAddress");
		if (!preferredAddress.isNull()) {
			person.address(preferredAddress.get("address1").asText());
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
			String display = attributes.get(i).get("display").asText();
			int index = display.indexOf("=");
			String name = display.substring(0, index).trim();
			String value = display.substring(index + 1).trim();
			Attribute attr = new Attribute(name, value);
			person.addAttribute(attr);
		}
	}
}

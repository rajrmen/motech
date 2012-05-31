package org.motechproject.openmrs.rest.util;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.DateMidnight;
import org.junit.Test;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSPerson;
import org.springframework.core.io.ClassPathResource;

public class JsonConverterUtilTest {
	
	private static final String PERSON_CREATE_JSON_FILE = "person-json.json";
	private static final String PERSON_RESPONE_JSON_FILE = "person-json-response.json";
	private DateMidnight date = new DateMidnight(2010, 5, 1);

	@Test
	public void shouldCreatePersonJson() throws JsonProcessingException, IOException {
		
		
		JsonNode expected = parseJsonFile(PERSON_CREATE_JSON_FILE);
		addDateOfBirth(expected);
		
		MRSPerson person = new MRSPerson();
		person.address("5 Main St")
			  .gender("M")
			  .birthDateEstimated(false)
			  .dead(false)
			  .firstName("John")
			  .middleName("E")
			  .lastName("Doe")
			  .dateOfBirth(date.toDate());
		JsonNode personJson = JsonConverterUtil.makeJsonPersonObjFromMrsPerson(person, true);
		
		assertTrue(expected.equals(personJson));
	}

    /**
     * Manually setting the date because hard coding it in json file might cause test failures
     * across different time zones
     * 
     * @param expected
     */
	private void addDateOfBirth(JsonNode expected) {
		((ObjectNode)expected).put("birthdate", DateUtil.formatToOpenMrsDate(date.toDate()));
    }

	private JsonNode parseJsonFile(String fileName) throws IOException, JsonProcessingException {
	    ObjectMapper mapper = new ObjectMapper();
		JsonNode expected = mapper.readTree(new ClassPathResource(fileName).getInputStream());

	    return expected;
    }
	
	@Test
	public void shouldParsePersonJson() throws JsonProcessingException, IOException {
		JsonNode personJson = parseJsonFile(PERSON_RESPONE_JSON_FILE);
		addDateOfBirth(personJson);
		MRSPerson convertedPerson = JsonConverterUtil.convertJsonToMrsPerson(personJson);
		MRSPerson expectedPerson = makeExpectedPerson();
		
		assertTrue(expectedPerson.equals(convertedPerson));
	}

	private MRSPerson makeExpectedPerson() {
		MRSPerson person = new MRSPerson();
		person.id("057e03ac-f8c7-47c2-9cbc-22bee016cfd3")
			  .address("5 Main St.")
			  .gender("M")
			  .preferredName("John Doe")
			  .birthDateEstimated(false)
			  .dead(false)
			  .firstName("John")
			  .middleName("E")
			  .lastName("Doe")
			  .birthDateEstimated(true)
			  .dateOfBirth(date.toDate());
		Attribute attr = new Attribute("Civil Status", "5555");
		person.addAttribute(attr);
		return person;
    }
}

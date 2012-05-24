package org.motechproject.openmrs.rest.impl;

import java.util.Calendar;
import java.util.Date;

import org.motechproject.mrs.model.MRSPerson;

public class TestUtils {
	protected static final String TEST_PERSON_ADDRESS = "5 Main St";
	protected static final String TEST_PERSON_GENDER = "M";
	protected static final String TEST_PERSON_LAST_NAME = "Trump";
	protected static final String TEST_PERSON_MIDDLE_NAME = "E";
	protected static final String TEST_PERSON_FIRST_NAME = "Donald";
	protected static final String MOTECH_ID_1 = "200-08";
	protected static Date CURRENT_DATE;
	
	static {
		Calendar instance = Calendar.getInstance();
		instance.set(Calendar.HOUR_OF_DAY, 0);
		instance.set(Calendar.MINUTE, 0);
		instance.set(Calendar.SECOND, 0);
		instance.set(Calendar.MILLISECOND, 0);
		
		CURRENT_DATE = instance.getTime();		
	}
	
	public static MRSPerson makePerson() {
    	MRSPerson person = new MRSPerson().firstName(TEST_PERSON_FIRST_NAME).middleName(TEST_PERSON_MIDDLE_NAME)
    			.lastName(TEST_PERSON_LAST_NAME).gender(TEST_PERSON_GENDER).address(TEST_PERSON_ADDRESS)
    			.dateOfBirth(CURRENT_DATE);
    	return person;
    }
}

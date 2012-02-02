package org.motechproject.CampaignDemo.dao;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.motechproject.openmrs.security.OpenMRSSession;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.motechproject.openmrs.Context;



public class PatientAuthentication {

	@Autowired
	private OpenMRSSession mrsSession;
	
	@Autowired
	private Context motechContext;
	
	@LoginAsAdmin
	public void login() {
		System.out.println("Annotation driven...");
		mrsSession.open();
		mrsSession.authenticate();
		
		UserService userService = motechContext.getUserService();
		PatientService patientService = motechContext.getPatientService();
		EncounterService encounterService = motechContext.getEncounterService();
		LocationService locationService = motechContext.getLocationService();
		ConceptService conceptService = motechContext.getConceptService();
		AdministrationService administrationService = motechContext.getAdministrationService();
		PersonService personService = motechContext.getPersonService();
		ObsService obsService = motechContext.getObsService();
		
		
		List<User> userList = userService.getUsers();
		for (User i : userList) {
			System.out.println(i.getUsername());
		}
		
		List<Patient> patientList = patientService.getAllPatients();
		for (Patient i: patientList) {
			System.out.println(i.toString());
		}
		
		
		mrsSession.close();
	}
	
	@LoginAsAdmin
	public void addPerson() {
		mrsSession.open();
		mrsSession.authenticate();
		PersonService personService = motechContext.getPersonService();
		Person newPerson = new Person();
		newPerson.setGender("M");
		newPerson.setBirthdate(new Date());
		personService.savePerson(newPerson);
		mrsSession.close();
	}
}

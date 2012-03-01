package org.motechproject.ScheduleTrackingDemo.beans;

import java.util.Date;

import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.validator.annotations.RegEx;
import org.motechproject.mobileforms.api.validator.annotations.Required;

public class PatientRegistrationBean extends FormBean {

	private static final long serialVersionUID = 1L;
	
	private String motechId;
	private String firstName;
	private String lastName;
	private String gender;
	private Date dateOfBirth;
	private String phoneNumber;
	private boolean enrollPatient;
	
	public String getMotechId() {
		return motechId;
	}
	
	public void setMotechId(String motechId) {
		this.motechId = motechId;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getGender() {
		return gender;
	}
	
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public Date getDateOfBirth() {
		return dateOfBirth;
	}
	
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public boolean isEnrollPatient() {
		return enrollPatient;
	}

	public void setEnrollPatient(boolean enrollPatient) {
		this.enrollPatient = enrollPatient;
	}
}

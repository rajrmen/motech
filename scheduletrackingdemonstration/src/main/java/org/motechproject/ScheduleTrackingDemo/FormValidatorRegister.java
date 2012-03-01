package org.motechproject.ScheduleTrackingDemo;

import javax.servlet.ServletContext;

import org.motechproject.ScheduleTrackingDemo.validator.PatientEnrollmentValidator;
import org.motechproject.ScheduleTrackingDemo.validator.PatientRegistrationValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

@Component
public class FormValidatorRegister implements ServletContextAware {
	

	private PatientRegistrationValidator patientRegValidator;
	private PatientEnrollmentValidator patientEnrollValidator;

	@Autowired
	public FormValidatorRegister(PatientRegistrationValidator patientRegValidator, PatientEnrollmentValidator patientEnrollValidator) {
		this.patientRegValidator = patientRegValidator;
		this.patientEnrollValidator = patientEnrollValidator;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		servletContext.setAttribute(PatientRegistrationValidator.class.getName(), patientRegValidator);
		servletContext.setAttribute(PatientEnrollmentValidator.class.getName(), patientEnrollValidator);
	}
}

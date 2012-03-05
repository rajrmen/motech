package org.motechproject.ScheduleTrackingDemo;

import javax.servlet.ServletContext;

import org.motechproject.ScheduleTrackingDemo.validator.PatientEncounterValidator;
import org.motechproject.ScheduleTrackingDemo.validator.PatientEnrollmentValidator;
import org.motechproject.ScheduleTrackingDemo.validator.PatientRegistrationValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

@Component
public class FormValidatorRegister implements ServletContextAware {

	private PatientRegistrationValidator patientRegValidator;
	private PatientEnrollmentValidator patientEnrollValidator;
	private PatientEncounterValidator patientEncounterValidator;

	@Autowired
	public FormValidatorRegister(
			PatientRegistrationValidator patientRegValidator,
			PatientEnrollmentValidator patientEnrollValidator,
			PatientEncounterValidator patientEncounterValidator) {
		this.patientRegValidator = patientRegValidator;
		this.patientEnrollValidator = patientEnrollValidator;
		this.patientEncounterValidator = patientEncounterValidator;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		servletContext.setAttribute(PatientRegistrationValidator.class.getName(), patientRegValidator);
		servletContext.setAttribute(PatientEnrollmentValidator.class.getName(), patientEnrollValidator);
		servletContext.setAttribute(PatientEncounterValidator.class.getName(), patientEncounterValidator);
	}
}

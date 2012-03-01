package org.motechproject.ScheduleTrackingDemo.listeners;

import org.motechproject.ScheduleTrackingDemo.OpenMrsClient;
import org.motechproject.ScheduleTrackingDemo.PatientScheduler;
import org.motechproject.ScheduleTrackingDemo.beans.PatientEnrollmentBean;
import org.motechproject.ScheduleTrackingDemo.beans.PatientRegistrationBean;
import org.motechproject.mobileforms.api.callbacks.FormPublisher;
import org.motechproject.model.MotechEvent;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


public class MobileFormListener {
	private static final String DEMO_SCHEDULE_NAME = "Demo Concept Schedule";

	Logger logger = LoggerFactory.getLogger(MobileFormListener.class);
	
	@Autowired
	OpenMrsClient openmrsClient;
	
	@Autowired
	PatientScheduler patientScheduler;
	
	@MotechListener(subjects = { FormPublisher.FORM_VALIDATION_SUCCESSFUL + ".DemoGroup.DemoPatientRegistration" })
	public void handlePatientRegistrationForm(MotechEvent event) {
		PatientRegistrationBean bean = (PatientRegistrationBean)event.getParameters().get(FormPublisher.FORM_BEAN);
		MRSPerson person = new MRSPerson().firstName(bean.getFirstName())
									 	  .lastName(bean.getLastName())
									 	  .dateOfBirth(bean.getDateOfBirth())
									 	  .birthDateEstimated(false)
									 	  .gender(bean.getGender());
		MRSFacility facility = new MRSFacility("1");
		MRSPatient patient = new MRSPatient(bean.getMotechId(), person, facility);
		
		openmrsClient.savePatient(patient);
		patientScheduler.saveMotechPatient(bean.getMotechId(), bean.getPhoneNumber());
		
		if (bean.isEnrollPatient()) {
			patientScheduler.enrollIntoSchedule(bean.getMotechId(), DEMO_SCHEDULE_NAME);
		}
	}
	
	@MotechListener(subjects = { FormPublisher.FORM_VALIDATION_SUCCESSFUL + ".DemoGroup.DemoPatientEnrollment" })
	public void handlePatientEnrollment(MotechEvent event) {
		PatientEnrollmentBean bean = (PatientEnrollmentBean)event.getParameters().get(FormPublisher.FORM_BEAN);
		patientScheduler.saveMotechPatient(bean.getMotechId(), bean.getPhoneNumber());
		patientScheduler.enrollIntoSchedule(bean.getMotechId(), DEMO_SCHEDULE_NAME);
	}
}

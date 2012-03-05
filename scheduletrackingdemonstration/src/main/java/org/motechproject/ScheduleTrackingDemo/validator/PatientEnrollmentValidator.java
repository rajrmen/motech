package org.motechproject.ScheduleTrackingDemo.validator;

import java.util.List;

import org.motechproject.ScheduleTrackingDemo.OpenMrsClient;
import org.motechproject.ScheduleTrackingDemo.beans.PatientEnrollmentBean;
import org.motechproject.mobileforms.api.domain.FormError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PatientEnrollmentValidator extends AbstractPatientValidator<PatientEnrollmentBean> {

	@Autowired
	public PatientEnrollmentValidator(OpenMrsClient openmrsClient) {
		super(openmrsClient);
	}

	@Override
	public List<FormError> validate(PatientEnrollmentBean formBean) {
		List<FormError> errors = super.validate(formBean);
		validatePhoneNumberFormat(formBean.getPhoneNumber(), errors);
		validateOpenMrsPatientExists(formBean.getMotechId(), errors);
		
		return errors;
	}
}

package org.motechproject.ScheduleTrackingDemo.validator;

import java.util.List;

import org.motechproject.ScheduleTrackingDemo.OpenMrsClient;
import org.motechproject.ScheduleTrackingDemo.beans.PatientEnrollmentBean;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mrs.model.MRSPatient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PatientEnrollmentValidator extends AbstractPatientValidator<PatientEnrollmentBean> {

	private OpenMrsClient openmrsClient;

	@Autowired
	public PatientEnrollmentValidator(OpenMrsClient openmrsClient) {
		this.openmrsClient = openmrsClient;
	}

	@Override
	public List<FormError> validate(PatientEnrollmentBean formBean) {
		List<FormError> errors = super.validate(formBean);
		validatePhoneNumberFormat(formBean.getPhoneNumber(), errors);
		validateOpenMrsPatientExists(formBean.getMotechId(), errors);
		
		return errors;
	}

	private void validateOpenMrsPatientExists(String motechId, List<FormError> errors) {
		MRSPatient existingPatient = openmrsClient.getPatientByMotechId(motechId);
		if (existingPatient == null) {
			errors.add(new FormError("motechId", "Could not find OpenMRS Patient with this MoTeCH Id"));
		}		
	}
}

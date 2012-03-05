package org.motechproject.ScheduleTrackingDemo.validator;

import java.util.List;

import org.motechproject.ScheduleTrackingDemo.OpenMrsClient;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mrs.model.MRSPatient;

public abstract class AbstractPatientValidator<V extends FormBean> extends AbstractMobileValidator<V> {

	protected OpenMrsClient openmrsClient;

	public AbstractPatientValidator(OpenMrsClient openmrsClient) {
		this.openmrsClient = openmrsClient;
	}

	protected void validateOpenMrsPatientExists(String motechId, List<FormError> errors) {
		MRSPatient existingPatient = openmrsClient.getPatientByMotechId(motechId);
		if (existingPatient == null) {
			errors.add(new FormError("motechId", "Could not find OpenMRS Patient with this MoTeCH Id"));
		}		
	}

}
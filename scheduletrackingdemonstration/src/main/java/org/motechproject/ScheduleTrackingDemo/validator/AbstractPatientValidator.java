package org.motechproject.ScheduleTrackingDemo.validator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mobileforms.api.validator.FormValidator;

public abstract class AbstractPatientValidator<V extends FormBean> extends FormValidator<V> {

	private static Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^[1-9][0-9]{2}-[1-9][0-9]{2}-[0-9]{4}$");
	
	protected void validatePhoneNumberFormat(String phoneNumber, List<FormError> errors) {
		Matcher matcher = PHONE_NUMBER_PATTERN.matcher(phoneNumber);
		if (!matcher.matches()) {
			errors.add(new FormError("phoneNumber", "Incorrect format for phone number. Format should be XXX-XXX-XXXX"));
		}
	}
}
package org.motechproject.server.web.ex;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;

public class StartupException extends RuntimeException {
    private List<String> errors;

    public StartupException(BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        errors = new ArrayList<>(allErrors.size());

        for (ObjectError error : allErrors) {
            errors.add(error.getCode());
        }
    }

    public List<String> getErrors() {
        return errors;
    }
}

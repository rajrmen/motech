package org.motechproject.server.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.validation.Errors;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

public class BootstrapConfigFormValidatorTest {

    @Mock
    private Errors errors;

    private BootstrapConfigFormValidator bootstrapConfigFormValidator = new BootstrapConfigFormValidator();

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldValidateOnlyForEmptyFields() {
        when(errors.hasFieldErrors("sqlUrl")).thenReturn(true);

        bootstrapConfigFormValidator.validate(new BootstrapConfigForm(), errors);

        verifyStatic();
    }

    @Test
    public void shouldValidateForDbUrlFormatIfNotEmpty() {
        when(errors.hasFieldErrors("sqlUrl")).thenReturn(false);
        when(errors.getFieldValue("sqlbUrl")).thenReturn("jdbc://localhost");

        bootstrapConfigFormValidator.validate(new BootstrapConfigForm(), errors);

        verifyStatic();
    }

    @Test
    public void shouldValidateForConfigSource(){
        when(errors.hasFieldErrors("sqlUrl")).thenReturn(false);
        when(errors.getFieldValue("sqlbUrl")).thenReturn("jdbc://localhost");

        BootstrapConfigForm bootstrapConfigForm = new BootstrapConfigForm();
        bootstrapConfigForm.setConfigSource("invalid");

        bootstrapConfigFormValidator.validate(bootstrapConfigForm, errors);

        verify(errors).rejectValue("invalid","server.error.invalid.configSource");
    }
}

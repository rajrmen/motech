package org.motechproject.server.web.controller;

import org.motechproject.security.service.MotechUserService;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.server.config.settings.ConfigFileSettings;
import org.motechproject.server.config.settings.MotechSettings;
import org.motechproject.server.startup.StartupManager;
import org.motechproject.server.ui.LocaleSettings;
import org.motechproject.server.web.ex.StartupException;
import org.motechproject.server.web.form.StartupForm;
import org.motechproject.server.web.form.StartupSuggestionsForm;
import org.motechproject.server.web.validator.StartupFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static java.util.Arrays.asList;
import static org.motechproject.security.helper.AuthenticationMode.REPOSITORY;
import static org.motechproject.server.config.settings.MotechSettings.AMQ_BROKER_URL;
import static org.motechproject.server.config.settings.MotechSettings.LANGUAGE;
import static org.motechproject.server.config.settings.MotechSettings.LOGINMODE;
import static org.motechproject.server.config.settings.MotechSettings.PROVIDER_NAME;
import static org.motechproject.server.config.settings.MotechSettings.PROVIDER_URL;
import static org.motechproject.server.config.settings.MotechSettings.SCHEDULER_URL;

@Controller
public class StartupController {

    private StartupManager startupManager = StartupManager.getInstance();

    @Autowired
    private PlatformSettingsService platformSettingsService;

    @Autowired
    private LocaleSettings localeSettings;

    @Autowired
    private MotechUserService userService;

    @RequestMapping(value = "/startup", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void submitForm(@RequestBody StartupForm form) {
        WebDataBinder binder = new WebDataBinder(form);
        binder.setValidator(new StartupFormValidator());
        binder.validate();

        BindingResult result = binder.getBindingResult();

        if (result.hasErrors()) {
            throw new StartupException(result);
        } else {
            ConfigFileSettings settings = startupManager.getLoadedConfig();
            settings.saveMotechSetting(LANGUAGE, form.getLanguage());
            settings.saveMotechSetting(SCHEDULER_URL, form.getSchedulerUrl());
            settings.saveMotechSetting(LOGINMODE, form.getLoginMode());
            settings.saveMotechSetting(PROVIDER_NAME, form.getProviderName());
            settings.saveMotechSetting(PROVIDER_URL, form.getProviderUrl());
            settings.saveActiveMqSetting(AMQ_BROKER_URL, form.getQueueUrl());

            platformSettingsService.savePlatformSettings(settings.getMotechSettings());
            platformSettingsService.saveActiveMqSettings(settings.getActivemqProperties());

            if (REPOSITORY.equals(form.getLoginMode())) {
                userService.register(
                        form.getAdminLogin(), form.getAdminPassword(), form.getAdminEmail(),
                        null, asList("Admin User", "Admin Bundle"), new Locale(form.getLanguage())
                );
            }

            startupManager.startup();
        }
    }

    @RequestMapping(value = "/startup/suggestions", method = RequestMethod.GET)
    @ResponseBody
    public StartupSuggestionsForm createSuggestions() {
        MotechSettings settings = startupManager.getLoadedConfig();
        StartupSuggestionsForm suggestions = new StartupSuggestionsForm();

        String queueUrl = settings.getActivemqProperties().getProperty(AMQ_BROKER_URL);
        String schedulerUrl = settings.getSchedulerProperties().getProperty(SCHEDULER_URL);

        if (startupManager.findActiveMQInstance(queueUrl)) {
            suggestions.addQueueSuggestion(queueUrl);
        }

        if (startupManager.findSchedulerInstance(schedulerUrl)) {
            suggestions.addSchedulerSuggestion(schedulerUrl);
        }

        return suggestions;
    }

    @ExceptionHandler(StartupException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public List<String> handleException(StartupException e) throws IOException {
        return e.getErrors();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleException(Exception e) throws IOException {
        return e.getMessage();
    }

}

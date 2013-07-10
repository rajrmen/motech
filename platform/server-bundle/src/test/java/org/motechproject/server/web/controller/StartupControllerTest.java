package org.motechproject.server.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.server.config.settings.ConfigFileSettings;
import org.motechproject.server.startup.StartupManager;
import org.motechproject.server.ui.LocaleSettings;
import org.motechproject.server.web.ex.StartupException;
import org.motechproject.server.web.form.StartupForm;
import org.motechproject.server.web.form.StartupSuggestionsForm;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.security.helper.AuthenticationMode.OPEN_ID;
import static org.motechproject.security.helper.AuthenticationMode.REPOSITORY;
import static org.motechproject.server.config.settings.MotechSettings.AMQ_BROKER_URL;
import static org.motechproject.server.config.settings.MotechSettings.SCHEDULER_URL;

@RunWith(PowerMockRunner.class)
@PrepareForTest({StartupManager.class})
public class StartupControllerTest {
    private static final String SUGGESTIONS_KEY = "suggestions";
    private static final String STARTUP_SETTINGS_KEY = "startupSettings";
    private static final String LANGUAGES_KEY = "languages";
    private static final String PAGE_LANG_KEY = "pageLang";

    @Mock
    private StartupManager startupManager;

    @Mock
    private PlatformSettingsService platformSettingsService;

    @Mock
    private LocaleSettings localeSettings;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private ConfigFileSettings motechSettings;

    @Mock
    private MotechUserService userService;

    @InjectMocks
    private StartupController startupController = new StartupController();

    @Before
    public void setUp() {
        PowerMockito.mockStatic(StartupManager.class);

        initMocks(this);

        when(StartupManager.getInstance()).thenReturn(startupManager);
    }

    @Test
    public void testSubmitFormStart() {
        StartupForm startupForm = startupForm(REPOSITORY);
        when(startupManager.getLoadedConfig()).thenReturn(motechSettings);
        when(startupManager.canLaunchBundles()).thenReturn(true);

        startupController.submitForm(startupForm);

        verify(platformSettingsService).savePlatformSettings(any(Properties.class));
        verify(startupManager).startup();
        verifyUserRegistration();
    }

    @Test
    public void testSubmitFormOpenId() {
        StartupForm startupForm = startupForm(OPEN_ID);
        when(startupManager.getLoadedConfig()).thenReturn(motechSettings);
        when(startupManager.canLaunchBundles()).thenReturn(true);

        startupController.submitForm(startupForm);

        verify(platformSettingsService).savePlatformSettings(any(Properties.class));
        verify(startupManager).startup();
        verify(userService, never()).register(anyString(), anyString(), anyString(), anyString(), anyListOf(String.class), any(Locale.class));
    }

    @Test(expected = StartupException.class)
    public void testSubmitThrowException() {
        startupController.submitForm(new StartupForm());
    }

    @Test
    public void testCreateSuggestions() {
        Properties properties = new Properties();
        properties.put("host", "localhost");
        properties.put("port", "12345");
        properties.put(AMQ_BROKER_URL, "test__amq_url");
        properties.put(SCHEDULER_URL, "test_scheduler_url");

        when(startupManager.getLoadedConfig()).thenReturn(motechSettings);
        when(motechSettings.getActivemqProperties()).thenReturn(properties);
        when(motechSettings.getSchedulerProperties()).thenReturn(properties);

        when(startupManager.findActiveMQInstance(properties.getProperty(AMQ_BROKER_URL))).thenReturn(false);
        when(startupManager.findSchedulerInstance(properties.getProperty(SCHEDULER_URL))).thenReturn(false);

        StartupSuggestionsForm suggestions = startupController.createSuggestions();

        assertTrue(suggestions.getDatabaseUrls().isEmpty());
        assertTrue(suggestions.getQueueUrls().isEmpty());
        assertTrue(suggestions.getSchedulerUrls().isEmpty());

        when(startupManager.findActiveMQInstance(properties.getProperty(AMQ_BROKER_URL))).thenReturn(true);
        when(startupManager.findSchedulerInstance(properties.getProperty(SCHEDULER_URL))).thenReturn(true);

        suggestions = startupController.createSuggestions();

        assertTrue(suggestions.getDatabaseUrls().isEmpty());
        assertThat(suggestions.getQueueUrls(), hasItem(properties.getProperty(AMQ_BROKER_URL)));
        assertThat(suggestions.getSchedulerUrls(), hasItem(properties.getProperty(SCHEDULER_URL)));
    }

    private StartupForm startupForm(String loginMode) {
        StartupForm startupForm = new StartupForm();

        startupForm.setLoginMode(loginMode);
        startupForm.setLanguage("en");
        startupForm.setQueueUrl("http://localhost/test_queue_url");
        startupForm.setSchedulerUrl("http://localhost/test_scheduler_url");
        startupForm.setAdminLogin("motech");
        startupForm.setAdminEmail("motech@motech.com");
        startupForm.setAdminPassword("motech");
        startupForm.setAdminConfirmPassword("motech");
        startupForm.setProviderName("Provider");
        startupForm.setProviderUrl("http://127.0.0.1/test_provider_url");

        return startupForm;
    }

    private void verifyUserRegistration() {
        verify(userService).register(eq("motech"), eq("motech"), eq("motech@motech.com"), eq((String) null),
                argThat(new ArgumentMatcher<List<String>>() {
            @Override
            public boolean matches(Object argument) {
                List<String> val = (List<String>) argument;
                return val.equals(Arrays.asList("Admin User", "Admin Bundle"));
            }
        }), eq(Locale.ENGLISH));
    }
}

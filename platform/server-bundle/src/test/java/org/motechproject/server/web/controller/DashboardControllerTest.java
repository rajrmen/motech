package org.motechproject.server.web.controller;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.commons.date.util.DateTimeSourceUtil;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.osgi.web.ModuleRegistrationData;
import org.motechproject.osgi.web.UIFrameworkService;
import org.motechproject.security.service.MotechRoleService;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.server.config.settings.ConfigFileSettings;
import org.motechproject.server.config.settings.MotechSettings;
import org.motechproject.server.startup.MotechPlatformState;
import org.motechproject.server.startup.StartupManager;
import org.motechproject.server.ui.LocaleSettings;
import org.motechproject.testing.utils.MockDateTimeSource;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Locale.ENGLISH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.osgi.web.UIFrameworkService.MODULES_WITHOUT_SUBMENU;
import static org.motechproject.osgi.web.UIFrameworkService.MODULES_WITH_SUBMENU;
import static org.motechproject.server.startup.MotechPlatformState.NEED_CONFIG;
import static org.motechproject.server.startup.MotechPlatformState.NORMAL_RUN;

@RunWith(PowerMockRunner.class)
@PrepareForTest({StartupManager.class})
public class DashboardControllerTest {
    private static final String OPEN_ID_PROVIDER_NAME = "name";
    private static final String OPEN_ID_PROVIDER_URL = "url";
    private static final String PAGE_TO_LOAD = "pageToLoad";
    private static final ModuleRegistrationData MODULE_WITH_SUBMENU = new ModuleRegistrationData("withSubMenu", "#/withSubMenu");
    private static final ModuleRegistrationData MODULE_WITHOUT_SUBMENU = new ModuleRegistrationData("withoutSubMenu", "#/withoutSubMenu");

    @Mock
    private StartupManager startupManager;

    @Mock
    private UIFrameworkService uiFrameworkService;

    @Mock
    private LocaleSettings localeSettings;

    @Mock
    private MotechUserService userService;

    @Mock
    private MotechRoleService roleService;

    @Mock
    private PlatformSettingsService settingsService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private DashboardController controller = new DashboardController();

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(StartupManager.class);
        initMocks(this);

        when(StartupManager.getInstance()).thenReturn(startupManager);

        Map<String, Collection<ModuleRegistrationData>> modules = new HashMap<>();
        modules.put(MODULES_WITH_SUBMENU, Arrays.asList(MODULE_WITH_SUBMENU));
        modules.put(MODULES_WITHOUT_SUBMENU, Arrays.asList(MODULE_WITHOUT_SUBMENU));

        when(uiFrameworkService.getRegisteredModules()).thenReturn(modules);
    }

    @Test
    public void shouldReturnStartupPageWhenPlatformNeedConfig() throws Exception {
        testCorrectView(NEED_CONFIG, "startup");
    }

    @Test
    public void shouldReturnLoginPageWhenUserHaveToLogin() throws Exception {
        testCorrectView(NORMAL_RUN, "login");
    }

    @Test
    public void shouldReturnAccessDeniedPageWhenUserHasNotPermission() throws Exception {
        testCorrectView(NORMAL_RUN, "accessdenied");
    }

    @Test
    public void shouldReturnHomePage() throws Exception {
        testCorrectView(NORMAL_RUN, "home");
    }

    @Test
    public void shouldReturnOpenIDProviderInformation() {
        MotechSettings settings = mock(ConfigFileSettings.class);

        when(settings.getProviderName()).thenReturn("provider_name");
        when(settings.getProviderUrl()).thenReturn("provider_url");
        when(settingsService.getPlatformSettings()).thenReturn(settings);

        Map<String, String> map = controller.getOpenIdProvider();

        assertEquals(2, map.size());
        assertEquals("provider_name", map.get(OPEN_ID_PROVIDER_NAME));
        assertEquals("provider_url", map.get(OPEN_ID_PROVIDER_URL));
    }

    @Test
    public void shouldReturnModulesWithSubMenu() {
        Principal principal = mock(Principal.class);

        when(principal.getName()).thenReturn("awesome");
        when(request.getUserPrincipal()).thenReturn(principal);
        when(localeSettings.getUserLocale(request)).thenReturn(ENGLISH);

        List<ModuleRegistrationData> modules = controller.getModulesWithSubMenu(request);

        assertFalse(modules.isEmpty());
        assertThat(modules, hasItem(MODULE_WITH_SUBMENU));
    }

    @Test
    public void shouldReturnModulesWithoutSubMenu() {
        when(localeSettings.getUserLocale(request)).thenReturn(ENGLISH);

        List<ModuleRegistrationData> modules = controller.getModulesWithoutSubMenu(request);

        assertFalse(modules.isEmpty());
        assertThat(modules, hasItem(MODULE_WITHOUT_SUBMENU));
    }

    @Test
    public void shouldReturnServerTime() {
        DateTime now = DateUtil.now();
        DateTimeSourceUtil.setSourceInstance(new MockDateTimeSource(now));

        String pattern = "EEE MMM dd, h:mm a, z yyyy";

        when(localeSettings.getUserLocale(request)).thenReturn(ENGLISH);

        assertEquals(
                now.toString(pattern),
                controller.getTime(request)
        );
    }

    private void testCorrectView(MotechPlatformState state, String pathInfo) {
        when(startupManager.getPlatformState()).thenReturn(state);
        when(request.getPathInfo()).thenReturn("/" + pathInfo);

        ModelAndView view = controller.index(request);

        assertEquals(pathInfo, view.getModel().get(PAGE_TO_LOAD));
    }
}

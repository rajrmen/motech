package org.motechproject.server.web.controller;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.osgi.web.ModuleRegistrationData;
import org.motechproject.osgi.web.UIFrameworkService;
import org.motechproject.security.model.RoleDto;
import org.motechproject.security.service.MotechRoleService;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.server.config.settings.MotechSettings;
import org.motechproject.server.startup.MotechPlatformState;
import org.motechproject.server.startup.StartupManager;
import org.motechproject.server.ui.LocaleSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.joda.time.format.DateTimeFormat.forPattern;
import static org.motechproject.commons.date.util.DateUtil.now;
import static org.motechproject.osgi.web.UIFrameworkService.MODULES_WITHOUT_SUBMENU;
import static org.motechproject.osgi.web.UIFrameworkService.MODULES_WITH_SUBMENU;

@Controller
public class DashboardController {
    private static final String OPEN_ID_PROVIDER_NAME = "name";
    private static final String OPEN_ID_PROVIDER_URL = "url";
    private static final String USER_NAME = "userName";
    private static final String SECURITY_LAUNCH = "securityLaunch";
    private static final String LANG = "lang";
    private static final String PAGE_TO_LOAD = "pageToLoad";

    private StartupManager startupManager = StartupManager.getInstance();

    @Autowired
    private UIFrameworkService uiFrameworkService;

    @Autowired
    private LocaleSettings localeSettings;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private MotechUserService userService;

    @Autowired
    private MotechRoleService roleService;

    @Autowired
    private PlatformSettingsService settingsService;


    @RequestMapping(value = {"/index", "/", "/home", "/login", "/accessdenied"}, method = RequestMethod.GET)
    public ModelAndView index(HttpServletRequest request) {
        ModelAndView view = new ModelAndView("index");
        String page = "home";

        if (startupManager.getPlatformState() == MotechPlatformState.NEED_CONFIG) {
            page = "startup";
        } else if (request.getPathInfo().startsWith("/login")) {
            page = "login";
        } else if (request.getPathInfo().startsWith("/accessdenied")) {
            page = "accessdenied";
        }

        view.addObject(PAGE_TO_LOAD, page);

        return view;
    }

    @RequestMapping(value = "/getOpenIdProvider", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> getOpenIdProvider() {
        Map<String, String> openIdProvider = new HashMap<>();
        MotechSettings settings = settingsService.getPlatformSettings();

        openIdProvider.put(OPEN_ID_PROVIDER_NAME, settings.getProviderName());
        openIdProvider.put(OPEN_ID_PROVIDER_URL, settings.getProviderUrl());

        return openIdProvider;
    }

    @RequestMapping(value = "/getModulesWithSubMenu", method = RequestMethod.POST)
    @ResponseBody
    public List<ModuleRegistrationData> getModulesWithSubMenu(HttpServletRequest request) {
        return filterPermittedModules(
                getUser(request).get(USER_NAME).toString(),
                uiFrameworkService.getRegisteredModules().get(MODULES_WITH_SUBMENU)
        );
    }

    @RequestMapping(value = "/getModulesWithoutSubMenu", method = RequestMethod.POST)
    @ResponseBody
    public List<ModuleRegistrationData> getModulesWithoutSubMenu(HttpServletRequest request) {
        return filterPermittedModules(
                getUser(request).get(USER_NAME).toString(),
                uiFrameworkService.getRegisteredModules().get(MODULES_WITHOUT_SUBMENU)
        );
    }

    @RequestMapping(value = "/getModule", method = RequestMethod.POST)
    @ResponseBody
    public ModuleRegistrationData getModule(@RequestBody String moduleName) {
        ModuleRegistrationData currentModule = null;

        if (StringUtils.isNotBlank(moduleName)) {
            currentModule = uiFrameworkService.getModuleData(moduleName);

            if (currentModule != null) {
                uiFrameworkService.moduleBackToNormal(moduleName);
            }
        }

        return currentModule;
    }

    @RequestMapping(value = "/gettime", method = RequestMethod.POST)
    @ResponseBody
    public String getTime(HttpServletRequest request) {
        Locale locale = localeSettings.getUserLocale(request);
        DateTimeFormatter format = forPattern("EEE MMM dd, h:mm a, z yyyy").withLocale(locale);
        return now().toString(format);
    }

    @RequestMapping(value = "/getUptime", method = RequestMethod.POST)
    @ResponseBody
    public DateTime getUptime() {
        return now().minus(ManagementFactory.getRuntimeMXBean().getUptime());
    }

    @RequestMapping(value = "/getUser", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getUser(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>(2);
        map.put(LANG, localeSettings.getUserLocale(request).getLanguage());

        if (request.getUserPrincipal() != null) {
            map.put(USER_NAME, request.getUserPrincipal().getName());
            map.put(SECURITY_LAUNCH, true);
        } else {
            map.put(USER_NAME, "Admin Mode");
            map.put(SECURITY_LAUNCH, false);
        }

        return map;
    }

    @RequestMapping(value = "/getContextPath", method = RequestMethod.POST)
    @ResponseBody
    public String getContextPath(HttpServletRequest request) {
        String contextPath = request.getSession().getServletContext().getContextPath();

        if (StringUtils.isNotBlank(contextPath) && !"/".equals(contextPath)) {
            contextPath = contextPath.substring(1) + "/";
        } else if (StringUtils.isBlank(contextPath) || "/".equals(contextPath)) {
            contextPath = "";
        }

        return contextPath;
    }

    private List<ModuleRegistrationData> filterPermittedModules(String userName, Collection<ModuleRegistrationData> modulesWithoutSubmenu) {
        List<ModuleRegistrationData> allowedModules = new ArrayList<>();

        if (modulesWithoutSubmenu != null) {
            for (ModuleRegistrationData registrationData : modulesWithoutSubmenu) {

                String requiredPermissionForAccess = registrationData.getRoleForAccess();

                if (requiredPermissionForAccess != null) {
                    if (checkUserPermission(userService.getRoles(userName), requiredPermissionForAccess)) {
                        allowedModules.add(registrationData);
                    }
                } else {
                    allowedModules.add(registrationData);
                }
            }
        }

        return allowedModules;
    }

    private boolean checkUserPermission(List<String> roles, String requiredPermission) {
        for (String userRole : roles) {
            RoleDto role = roleService.getRole(userRole);
            if (role != null) {
                if (role.getPermissionNames() != null && role.getPermissionNames().contains(requiredPermission)) {
                    return true;
                }
            }
        }

        return false;
    }

}

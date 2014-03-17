package org.motechproject.server.web.controller;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.osgi.web.ModuleRegistrationData;
import org.motechproject.osgi.web.UIFrameworkService;
import org.motechproject.osgi.web.util.BundleHeaders;
import org.motechproject.server.startup.StartupManager;
import org.motechproject.server.ui.LocaleService;
import org.motechproject.server.web.dto.ModuleConfig;
import org.motechproject.server.web.dto.ModuleMenu;
import org.motechproject.server.web.form.UserInfo;
import org.motechproject.server.web.helper.MenuBuilder;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.joda.time.format.DateTimeFormat.forPattern;
import static org.motechproject.commons.date.util.DateUtil.now;

/**
 * Main application controller. Responsible for retrieving information shared across the UI of different modules.
 * The view returned by this controller will embed the UI of the currently requested module.
 */
@Controller
public class DashboardController {
    private static final String MODULE_NAME = "('|\")(?<name>[^'\"])+('|\")";
    private static final String REQUIRES = "\\[(?<requires>[^\\]])+\\]";
    private static final Pattern PATTERN = Pattern.compile(
            String.format("angular.module(%s,\\s+%s)", MODULE_NAME, REQUIRES)
    );

    @Autowired
    private StartupManager startupManager;

    @Autowired
    private UIFrameworkService uiFrameworkService;

    @Autowired
    private LocaleService localeService;

    @Autowired
    private MenuBuilder menuBuilder;

    @Autowired
    private BundleContext bundleContext;

    @Autowired
    @Qualifier("mainHeaderStr")
    private String mainHeader;

    @RequestMapping({"/index", "/", "/home"})
    public ModelAndView index(@RequestParam(required = false) String moduleName, final HttpServletRequest request) {
        ModelAndView mav;

        // check if this is the first run
        if (startupManager.isConfigRequired()) {
            mav = new ModelAndView(Constants.REDIRECT_STARTUP);
        } else {
            mav = new ModelAndView("index");
            mav.addObject("isAccessDenied", false);
            mav.addObject("loginPage", false);
            mav.addObject("mainHeader", mainHeader);
            String contextPath = request.getSession().getServletContext().getContextPath();

            if (StringUtils.isNotBlank(contextPath) && !"/".equals(contextPath)) {
                mav.addObject("contextPath", contextPath.substring(1) + "/");
            } else if (StringUtils.isBlank(contextPath) || "/".equals(contextPath)) {
                mav.addObject("contextPath", "");
            }

            if (moduleName != null) {
                ModuleRegistrationData currentModule = uiFrameworkService.getModuleData(moduleName);
                if (currentModule != null) {
                    mav.addObject("currentModule", currentModule);
                    mav.addObject("criticalNotification", currentModule.getCriticalMessage());
                    uiFrameworkService.moduleBackToNormal(moduleName);
                }
            }
        }

        return mav;
    }

    @RequestMapping(value = "/accessdenied", method = RequestMethod.GET)
    public ModelAndView accessdenied(final HttpServletRequest request) {
        ModelAndView view = index(null, request);
        view.addObject("isAccessDenied", true);
        view.addObject("loginPage", false);
        return view;
    }

    @RequestMapping(value = "/modulemenu", method = RequestMethod.GET)
    @ResponseBody
    public ModuleMenu getModuleMenu(HttpServletRequest request) {
        String username = getUser(request).getUserName();
        return menuBuilder.buildMenu(username);
    }

    @RequestMapping(value = "/moduleconfig", method = RequestMethod.GET)
    @ResponseBody
    public List<ModuleConfig> getModuleConfig() throws IOException {
        List<ModuleConfig> configuration = new ArrayList<>();

        for (Bundle bundle : bundleContext.getBundles()) {
            BundleHeaders headers = new BundleHeaders(bundle);
            ModuleRegistrationData data = uiFrameworkService.getModuleDataByBundle(bundle);

            if (null != data) {
                Enumeration<URL> entries = bundle.findEntries("/webapp", "*.js", true);
                Map<String, String> scripts = new HashMap<>();
                List<String> dependencies = new ArrayList<>();

                while (entries.hasMoreElements()) {
                    URL entry = entries.nextElement();
                    String path = entry.getPath();
                    int idx = path.indexOf("/js/");

                    if (idx > 0) {
                        path = path.substring(idx);
                    }

                    String filename = FilenameUtils.getBaseName(path);
                    StringWriter writer = new StringWriter();

                    IOUtils.copy(entry.openStream(), writer);
                    String content = writer.toString().replace("\n", "");
                    Matcher matcher = PATTERN.matcher(content);

                    while (matcher.find()) {
                        if (filename.equalsIgnoreCase("app")) {
                            String requires = matcher.group("requires");
                            requires = requires.replaceAll("('|\"|\\s+)", requires);

                            String[] elements = requires.split(",");

                            Collections.addAll(dependencies, elements);
                        }

                        String name = matcher.group("name");

                        if (!scripts.containsKey(name)) {
                            scripts.put(name, path);
                        }
                    }
                }

                List<String> angularModules = data.getAngularModules();

                ModuleConfig module = new ModuleConfig();
                module.setName(isEmpty(angularModules) ? null : angularModules.get(0));
                module.setScript("../" + headers.getResourcePath() + "/js/app.js");
                module.setTemplate(data.getUrl());

                configuration.add(module);

                for (String dependency : dependencies) {
                    if (scripts.containsKey(dependency)) {
                        ModuleConfig depConfig = new ModuleConfig();
                        depConfig.setName(dependency);
                        depConfig.setScript("../" + headers.getResourcePath() + scripts.get(dependency));

                        configuration.add(depConfig);
                    }
                }

            }
        }

        return configuration;
    }

    @RequestMapping(value = "/gettime", method = RequestMethod.POST)
    @ResponseBody
    public String getTime(HttpServletRequest request) {
        Locale locale = localeService.getUserLocale(request);
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
    public UserInfo getUser(HttpServletRequest request) {
        String lang = localeService.getUserLocale(request).getLanguage();
        boolean securityLaunch = request.getUserPrincipal() != null;
        String userName = securityLaunch ? request.getUserPrincipal().getName() : "Admin Mode";

        return new UserInfo(userName, securityLaunch, lang);
    }

}

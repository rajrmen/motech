package org.motechproject.server.web.controller;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.motechproject.commons.api.CastUtils;
import org.motechproject.osgi.web.ModuleRegistrationData;
import org.motechproject.osgi.web.UIFrameworkService;
import org.motechproject.osgi.web.util.BundleHeaders;
import org.motechproject.server.web.dto.ModuleConfig;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang.StringUtils.isNotBlank;

@Controller
public class ModuleController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModuleController.class);

    private static final String NAME_GROUP = "name";
    private static final String DEPENDENCIES_GROUP = "dependencies";
    private static final String MODULE_NAME = String.format(
            "('|\")(?<%s>[^'\"]+)('|\")", NAME_GROUP
    );
    private static final String DEPENDENCIES = String.format(
            "\\[(?<%s>[^\\]]*)\\]", DEPENDENCIES_GROUP
    );
    private static final String REGEXP = String.format(
            "angular\\.module\\(%s(,\\s+%s)?\\)", MODULE_NAME, DEPENDENCIES
    );
    private static final Pattern PATTERN = Pattern.compile(REGEXP);

    private UIFrameworkService uiFrameworkService;
    private BundleContext bundleContext;

    @RequestMapping(value = "/module/config", method = RequestMethod.GET)
    @ResponseBody
    public List<ModuleConfig> getModuleConfig() throws IOException {
        List<ModuleConfig> configuration = new ArrayList<>();

        for (Bundle bundle : bundleContext.getBundles()) {
            ModuleRegistrationData data = uiFrameworkService.getModuleDataByBundle(bundle);

            if (null != data) {
                BundleHeaders headers = new BundleHeaders(bundle);
                Map<String, String> scripts = new HashMap<>();
                List<String> requires = new ArrayList<>();

                findScripts(bundle, scripts, requires);

                List<String> angularModules = data.getAngularModules();
                String name = isEmpty(angularModules) ? null : angularModules.get(0);

                addConfig(configuration, headers, name, "/js/app.js", data.getUrl());

                for (String require : requires) {
                    if (scripts.containsKey(require)) {
                        addConfig(configuration, headers, require, scripts.get(require));
                    }
                }
            }
        }

        return configuration;
    }

    private void findScripts(Bundle bundle, Map<String, String> scripts, List<String> requires) {
        List<URL> entries = getEntries(bundle);

        for (URL entry : entries) {
            String content = getContent(entry);
            String path = getPath(entry);
            String filename = FilenameUtils.getBaseName(path);

            Matcher matcher = PATTERN.matcher(content);

            while (matcher.find()) {
                if (filename.equalsIgnoreCase("app")) {
                    String[] dependencies = getDependencies(matcher);
                    Collections.addAll(requires, dependencies);
                }

                String name = matcher.group(NAME_GROUP);

                if (!scripts.containsKey(name)) {
                    scripts.put(name, path);
                }
            }
        }
    }

    private void addConfig(List<ModuleConfig> configuration, BundleHeaders headers, String name,
                           String script) {
        addConfig(configuration, headers, name, script, null);
    }

    private void addConfig(List<ModuleConfig> configuration, BundleHeaders headers, String name,
                           String script, String template) {
        ModuleConfig config = new ModuleConfig();
        config.setName(name);
        config.setScript("../" + headers.getResourcePath() + script);
        config.setTemplate(template);

        if (isNotBlank(name)) {
            configuration.add(config);
        }
    }

    private String getContent(URL url) {
        StringWriter writer = new StringWriter();
        String content;
        try {
            IOUtils.copy(url.openStream(), writer);
            content = writer.toString();
        } catch (IOException e) {
            LOGGER.error("There were problems with read entry: {}", url.getPath(), e);
            content = EMPTY;
        }

        return content.replace("\n", "");
    }

    private String getPath(URL url) {
        String path = url.getPath();
        int idx = path.indexOf("/js/");

        if (idx > 0) {
            path = path.substring(idx);
        }

        return path;
    }

    private List<URL> getEntries(Bundle bundle) {
        Enumeration enumeration = bundle.findEntries("/webapp/js", "*.js", true);
        return CastUtils.cast(URL.class, enumeration);
    }

    private String[] getDependencies(Matcher matcher) {
        String requires = matcher.group(DEPENDENCIES_GROUP);
        requires = requires.replaceAll("('|\"|\\s+)", "");

        return requires.split(",");
    }

    @Autowired
    public void setUiFrameworkService(UIFrameworkService uiFrameworkService) {
        this.uiFrameworkService = uiFrameworkService;
    }

    @Autowired
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }
}

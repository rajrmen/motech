package org.motechproject.server.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.HashMap;
import java.util.Map;

public class OsgiListener implements ServletContextListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(OsgiListener.class);

    private static Map<String, OsgiFrameworkService> services = new HashMap<>();

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        LOGGER.debug("Starting OSGi framework...");

        ApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(
                servletContextEvent.getServletContext());

        services = applicationContext.getBeansOfType(OsgiFrameworkService.class);

        for (OsgiFrameworkService ofs : services.values()) {
            ofs.start();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        for (OsgiFrameworkService ofs : services.values()) {
            ofs.stop();
        }
    }

    public static OsgiFrameworkService getOsgiService(String serviceName) {
        return services.get(serviceName);
    }
}

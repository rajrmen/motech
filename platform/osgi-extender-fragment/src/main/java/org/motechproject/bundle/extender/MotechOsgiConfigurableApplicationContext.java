package org.motechproject.bundle.extender;

import org.eclipse.gemini.blueprint.context.support.OsgiBundleXmlApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public class MotechOsgiConfigurableApplicationContext extends OsgiBundleXmlApplicationContext implements ConfigurableWebApplicationContext {

    private ServletContext servletContext;
    private ServletConfig servletConfig;
    private String namespace;

    private boolean initialized;
    private final Object lock = new Object();

    public MotechOsgiConfigurableApplicationContext(String[] configurationLocations) {
        super(configurationLocations);
        addApplicationListener(new ApplicationListener<ApplicationEvent>() {
            @Override
            public void onApplicationEvent(ApplicationEvent event) {
                if (event instanceof ContextRefreshedEvent) {
                    synchronized (lock) {
                        initialized = true;
                        lock.notifyAll();
                    }
                }
            }
        });
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public void setServletConfig(ServletConfig servletConfig) {
        this.servletConfig = servletConfig;
    }

    @Override
    public ServletConfig getServletConfig() {
        return this.servletConfig;
    }

    @Override
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public void setConfigLocation(String configLocation) {
        this.setConfigLocations(new String[]{configLocation});
    }

    public boolean isInitialized() {
        return initialized;
    }

    public Object getLock() {
        return lock;
    }
}

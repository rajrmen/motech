package org.motechproject.server.pillreminder.api.osgi;

import org.motechproject.osgi.web.MotechOsgiWebApplicationContext;
import org.motechproject.tasks.service.ChannelService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.InputStream;

public class Activator implements BundleActivator {
    private static Logger logger = LoggerFactory.getLogger(Activator.class);
    private static final String CONTEXT_CONFIG_LOCATION = "applicationPillReminderAPIBundle.xml";
    private static final String SERVLET_URL_MAPPING = "/pillReminder";

    private ServiceTracker httpServiceTracker;
    private ServiceTracker channelServiceTracker;

    private static BundleContext bundleContext;

    @Override
    public void start(BundleContext context) throws Exception {
        bundleContext = context;

        this.httpServiceTracker = new ServiceTracker(context,
                HttpService.class.getName(), null) {

            @Override
            public Object addingService(ServiceReference ref) {
                Object service = super.addingService(ref);
                serviceAdded((HttpService) service);
                return service;
            }

            @Override
            public void removedService(ServiceReference ref, Object service) {
                serviceRemoved((HttpService) service);
                super.removedService(ref, service);
            }
        };
        this.httpServiceTracker.open();

        this.channelServiceTracker = new ServiceTracker(context,
                ChannelService.class.getName(), null) {

            @Override
            public Object addingService(ServiceReference ref) {
                Object service = super.addingService(ref);
                serviceAdded((ChannelService) service);
                return service;
            }

        };
        this.channelServiceTracker.open();
    }

    public void stop(BundleContext context) throws Exception {
        this.httpServiceTracker.close();
        this.channelServiceTracker.close();
    }

    public static class PillReminderApplicationContext extends MotechOsgiWebApplicationContext {

        public PillReminderApplicationContext() {
            super();
            setBundleContext(Activator.bundleContext);
        }
    }

    private void serviceAdded(HttpService service) {
        try {
            DispatcherServlet dispatcherServlet = new DispatcherServlet();
            dispatcherServlet.setContextConfigLocation(CONTEXT_CONFIG_LOCATION);
            dispatcherServlet.setContextClass(PillReminderApplicationContext.class);
            ClassLoader old = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
                service.registerServlet(SERVLET_URL_MAPPING, dispatcherServlet, null, null);
                logger.debug("Servlet registered");
            } finally {
                Thread.currentThread().setContextClassLoader(old);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void serviceRemoved(HttpService service) {
        service.unregister(SERVLET_URL_MAPPING);
        logger.debug("Servlet unregistered");
    }

    private void serviceAdded(ChannelService service) {
        try {
            InputStream stream = getClass().getClassLoader().getResourceAsStream("task-channel.json");
            service.registerChannel(stream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

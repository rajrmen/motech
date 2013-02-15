package org.motechproject.osgi.web;

import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.event.listener.annotations.EventAnnotationBeanPostProcessor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

import static java.lang.String.format;

public class EventListenerRegistrar {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventListenerRegistrar.class);


    private Map<String, Object> processedBeans = new HashMap<>();
    private Queue<ApplicationContext> contextsToBeProcessed = new ConcurrentLinkedDeque<>();
    private BundleContext context;
    private EventAnnotationBeanPostProcessor beanPostProcessor;


    public EventListenerRegistrar(BundleContext context) {
        this.context = context;
    }

    public EventListenerRegistrar start() {
        new ServiceTracker(context, EventListenerRegistryService.class.getName(), null) {
            @Override
            public Object addingService(ServiceReference reference) {
                Object service = super.addingService(reference);
                beanPostProcessor = new EventAnnotationBeanPostProcessor((EventListenerRegistryService) service);
                processContextsInQueue();
                return service;
            }
        }.open();
        return this;
    }


    public void registerEventAnnotatedBeansIn(ApplicationContext applicationContext) {
        if (beanPostProcessor == null) {
            contextsToBeProcessed.add(applicationContext);
            LOGGER.debug("Added context to queue  " + applicationContext.getDisplayName());
            return;
        }

        processAnnotations(applicationContext);
    }

    public void unregisterEventAnnotatedBeansIn(ApplicationContext applicationContext) {
        for (String name : applicationContext.getBeanDefinitionNames()) {
            beanPostProcessor.clearListenerForBean(name);
            processedBeans.remove(generateKey(applicationContext, name));
        }
    }

    private void processAnnotations(ApplicationContext applicationContext) {
        String[] definitionNames = applicationContext.getBeanDefinitionNames();
        for (String definitionName : definitionNames) {
            String beanKey = generateKey(applicationContext, definitionName);
            if (processedBeans.containsKey(beanKey)) {
                continue;
            }
            Object bean = applicationContext.getBean(definitionName);
            beanPostProcessor.processAnnotations(bean, definitionName);
            processedBeans.put(beanKey, bean);
        }
    }


    private void processContextsInQueue() {
        while (!contextsToBeProcessed.isEmpty()) {
            registerEventAnnotatedBeansIn(contextsToBeProcessed.poll());
        }
    }


    private String generateKey(ApplicationContext applicationContext, String definitionName) {
        return format("%s-%s", applicationContext.getId(), definitionName);
    }


}

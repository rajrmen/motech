package org.motechproject.osgi;

import org.motechproject.commons.api.ApplicationContextServiceReferenceUtils;
import org.motechproject.server.config.annotations.ConfigUpdateListener;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ManagedService;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class BlueprintApplicationContextTracker extends ServiceTracker {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlueprintApplicationContextTracker.class);

    private List<String> contextsProcessed = Collections.synchronizedList(new ArrayList<String>());

    public BlueprintApplicationContextTracker(BundleContext bundleContext) {
        super(bundleContext, ApplicationContext.class.getName(), null);
    }

    @Override
    public Object addingService(ServiceReference serviceReference) {
        ApplicationContext applicationContext = (ApplicationContext) super.addingService(serviceReference);
        LOGGER.error("Staring to process " + applicationContext.getDisplayName());

        if (ApplicationContextServiceReferenceUtils.isNotValid(serviceReference)) {
            return applicationContext;
        }

        if (contextsProcessed.contains(applicationContext.getId())) {
            return applicationContext;
        }

        String[] definitionNames = applicationContext.getBeanDefinitionNames();
        for (String definitionName : definitionNames) {
            registerConfigurationUpdateListeners(applicationContext.getBean(definitionName));
        }

        contextsProcessed.add(applicationContext.getId());

        LOGGER.debug("Processed " + applicationContext.getDisplayName());

        return applicationContext;
    }

    @Override
    public void removedService(ServiceReference reference, Object service) {
        super.removedService(reference, service);
        ApplicationContext applicationContext = (ApplicationContext) service;

        if (ApplicationContextServiceReferenceUtils.isValid(reference)) {
            contextsProcessed.remove(applicationContext.getId());
        }
    }


    private void registerConfigurationUpdateListeners(final Object bean) {
        ReflectionUtils.doWithMethods(AopUtils.getTargetClass(bean), new ReflectionUtils.MethodCallback() {

            @Override
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                Method methodOfOriginalClassIfProxied = ReflectionUtils.findMethod(AopUtils.getTargetClass(bean), method.getName(), method.getParameterTypes());
                if (methodOfOriginalClassIfProxied != null) {
                    ConfigUpdateListener annotation = methodOfOriginalClassIfProxied.getAnnotation(ConfigUpdateListener.class);
                    if (annotation != null) {
                        Properties properties = new Properties();
                        properties.setProperty(Constants.SERVICE_PID, annotation.pid());
                        context.registerService(ManagedService.class.getName(), new ManagedServiceProxy(methodOfOriginalClassIfProxied, bean), properties);
                    }
                }
            }
        });
    }


}

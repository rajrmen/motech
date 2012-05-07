package org.motechproject.server.event.annotations;

import org.motechproject.context.Context;
import org.motechproject.server.event.EventListenerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.springframework.util.ReflectionUtils.doWithMethods;
import static org.springframework.util.ReflectionUtils.findMethod;

/**
 * Responsible for registering handlers based on annotations
 */
@Component
public class EventAnnotationBeanPostProcessor implements BeanPostProcessor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /* (non-Javadoc)
      * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessBeforeInitialization(java.lang.Object, java.lang.String)
      */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    /* (non-Javadoc)
      * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization(java.lang.Object, java.lang.String)
      */
    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        doWithMethods(bean.getClass(), new ReflectionUtils.MethodCallback() {
            @Override
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                Method methodOfOriginalClassIfProxied = findMethod(AopUtils.getTargetClass(bean), method.getName(), method.getParameterTypes());
                if (methodOfOriginalClassIfProxied != null) {
                    MotechListener motechListenerAnnotation;
                    if ((motechListenerAnnotation = methodOfOriginalClassIfProxied.getAnnotation(MotechListener.class)) != null) {
                        RegisterEventListener(method, motechListenerAnnotation, beanName, bean);
                    }

                    MotechErrorListener motechErrorListenerAnnotation;
                    if ((motechErrorListenerAnnotation = methodOfOriginalClassIfProxied.getAnnotation(MotechErrorListener.class)) != null) {
                        RegisterErrorEventListener(method, motechErrorListenerAnnotation, beanName, bean);
                    }
                }
            }
        });
        return bean;
    }

    private void RegisterErrorEventListener(Method method, MotechErrorListener motechListenerAnnotation, String beanName, Object bean) {
        final List<String> subjects = Arrays.asList(motechListenerAnnotation.subjects());
        MotechListenerAbstractProxy proxy = getProxy(method, beanName, bean, motechListenerAnnotation.type());
        logger.info(String.format("Registering event error listener type(%20s) bean: %s , method: %s, for subjects: %s", motechListenerAnnotation.type().toString(), beanName, method.toGenericString(), subjects));
        EventListenerRegistry eventErrorListenerRegistry = Context.getInstance().getEventErrorListenerRegistry();
        if (eventErrorListenerRegistry != null)
            eventErrorListenerRegistry.registerListener(proxy, subjects);
    }

    private void RegisterEventListener(Method method, MotechListener motechListenerAnnotation, String beanName, Object bean) {
        final List<String> subjects = Arrays.asList(motechListenerAnnotation.subjects());
        MotechListenerAbstractProxy proxy = getProxy(method, beanName, bean, motechListenerAnnotation.type());
        logger.info(String.format("Registering listener type(%20s) bean: %s , method: %s, for subjects: %s", motechListenerAnnotation.type().toString(), beanName, method.toGenericString(), subjects));
        EventListenerRegistry eventListenerRegistry = Context.getInstance().getEventListenerRegistry();
        if (eventListenerRegistry != null)
            eventListenerRegistry.registerListener(proxy, subjects);
    }

    private MotechListenerAbstractProxy getProxy(Method method, String beanName, Object bean, MotechListenerType motechListenerType) {
        MotechListenerAbstractProxy proxy = null;
        switch (motechListenerType) {
            case ORDERED_PARAMETERS:
                proxy = new MotechListenerOrderedParametersProxy(beanName, bean, method);
                break;
            case MOTECH_EVENT:
                proxy = new MotechListenerEventProxy(beanName, bean, method);
                break;
            case NAMED_PARAMETERS:
                proxy = new MotechListenerNamedParametersProxy(beanName, bean, method);
                break;
        }
        return proxy;
    }

    /**
     * Registers event handlers (hack because we are running spring embedded in an OSGi module)
     */
    public static void registerHandlers(Map<String, Object> beans) {
        EventAnnotationBeanPostProcessor processor = new EventAnnotationBeanPostProcessor();
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            processor.postProcessAfterInitialization(entry.getValue(), entry.getKey());
        }
    }
}

package org.motechproject.mds.service;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ClassLoaderAspect {

    @Around("within(org.motechproject.mds.service.impl.*)")
    public void changeClassLoader(ProceedingJoinPoint joinPoint) throws Throwable {
        ClassLoader webAppClassLoader = Thread.currentThread().getContextClassLoader();
        JDOClassLoader persistenceClassLoader = new JDOClassLoader(getClass().getClassLoader());

        Thread.currentThread().setContextClassLoader(persistenceClassLoader);
        JDOClassLoader enhancerClassLoader = new JDOClassLoader(persistenceClassLoader);

        Object target = joinPoint.getTarget();
        BaseMdsService baseMdsService = null;

        if (target instanceof BaseMdsService) {
            baseMdsService = (BaseMdsService) target;
            baseMdsService.setEnhancerClassLoader(enhancerClassLoader);
            baseMdsService.setPersistenceClassLoader(persistenceClassLoader);
        }

        joinPoint.proceed();

        if (null != baseMdsService) {
            baseMdsService.setEnhancerClassLoader(null);
            baseMdsService.setPersistenceClassLoader(null);
        }

        Thread.currentThread().setContextClassLoader(webAppClassLoader);
    }
}

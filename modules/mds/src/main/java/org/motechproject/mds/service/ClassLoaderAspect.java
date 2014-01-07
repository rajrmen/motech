package org.motechproject.mds.service;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class ClassLoaderAspect {

    @Around("within(org.motechproject.mds.service.impl.*)")
    public void changeClassLoader(ProceedingJoinPoint joinPoint) throws Throwable {
        ClassLoader webAppClassLoader = Thread.currentThread().getContextClassLoader();
        JDOClassLoader persistenceClassLoader = new JDOClassLoader(getClass().getClassLoader());

        Thread.currentThread().setContextClassLoader(persistenceClassLoader);
        JDOClassLoader enhancerClassLoader = new JDOClassLoader(persistenceClassLoader);

        Object target = joinPoint.getTarget();

        if (target instanceof BaseMdsService) {
            BaseMdsService baseMdsService = (BaseMdsService) target;
            baseMdsService.setEnhancerClassLoader(enhancerClassLoader);
            baseMdsService.setPersistenceClassLoader(persistenceClassLoader);
        }

        joinPoint.proceed();

        Thread.currentThread().setContextClassLoader(webAppClassLoader);
    }
}

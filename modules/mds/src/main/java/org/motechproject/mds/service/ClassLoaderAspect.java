package org.motechproject.mds.service;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.motechproject.mds.service.BaseMdsService;
import org.motechproject.mds.service.JDOClassLoader;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ClassLoaderAspect {

    @Around("within(org.motechproject.mds.service.impl.*)")
    public void changeClassLoader(ProceedingJoinPoint joinPoint) throws Throwable {
        Object target = joinPoint.getTarget();

        if (!(target instanceof BaseMdsService)) {
            throw new IllegalStateException(
                    "The target class should extend " + BaseMdsService.class.getName()
            );
        }

        ClassLoader webAppClassLoader = Thread.currentThread().getContextClassLoader();
        JDOClassLoader persistenceClassLoader = new JDOClassLoader(getClass().getClassLoader());

        Thread.currentThread().setContextClassLoader(persistenceClassLoader);
        JDOClassLoader enhancerClassLoader = new JDOClassLoader(persistenceClassLoader);

        BaseMdsService baseMdsService = (BaseMdsService) target;
        baseMdsService.setEnhancerClassLoader(enhancerClassLoader);
        baseMdsService.setPersistenceClassLoader(persistenceClassLoader);

        joinPoint.proceed();

        baseMdsService.setEnhancerClassLoader(null);
        baseMdsService.setPersistenceClassLoader(null);

        Thread.currentThread().setContextClassLoader(webAppClassLoader);
    }
}

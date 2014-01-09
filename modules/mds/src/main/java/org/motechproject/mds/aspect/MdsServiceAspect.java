package org.motechproject.mds.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.motechproject.mds.service.BaseMdsService;
import org.motechproject.mds.service.JDOClassLoader;
import org.springframework.stereotype.Component;

import java.security.AccessController;
import java.security.PrivilegedAction;

@Aspect
@Component
public class MdsServiceAspect {

    @Around("within(org.motechproject.mds.service.impl.*)")
    public Object changeClassLoader(ProceedingJoinPoint joinPoint) throws Throwable { // NO CHECKSTYLE IllegalThrowsCheck
        Object target = joinPoint.getTarget();

        if (!(target instanceof BaseMdsService)) {
            throw new IllegalStateException(
                    "The target class should extend " + BaseMdsService.class.getName()
            );
        }

        ClassLoader webAppClassLoader = Thread.currentThread().getContextClassLoader();
        BaseMdsService baseMdsService = (BaseMdsService) target;

        try {
            JDOClassLoader persistenceClassLoader = createClassLoader(getClass().getClassLoader());

            Thread.currentThread().setContextClassLoader(persistenceClassLoader);
            JDOClassLoader enhancerClassLoader = createClassLoader(persistenceClassLoader);

            baseMdsService.setEnhancerClassLoader(enhancerClassLoader);
            baseMdsService.setPersistenceClassLoader(persistenceClassLoader);

            return joinPoint.proceed();
        } finally {
            baseMdsService.setEnhancerClassLoader(null);
            baseMdsService.setPersistenceClassLoader(null);

            Thread.currentThread().setContextClassLoader(webAppClassLoader);
        }
    }

    private JDOClassLoader createClassLoader(final ClassLoader parent) {
        return AccessController.doPrivileged(new PrivilegedAction<JDOClassLoader>() {
            @Override
            public JDOClassLoader run() {
                return new JDOClassLoader(parent);
            }
        });
    }
}

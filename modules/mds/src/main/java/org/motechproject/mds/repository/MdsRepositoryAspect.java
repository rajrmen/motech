package org.motechproject.mds.repository;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MdsRepositoryAspect {

    @Around("within(org.motechproject.mds.repository.All*)")
    public void changeClassLoader(ProceedingJoinPoint joinPoint) throws Throwable {
        Object target = joinPoint.getTarget();

        if (!(target instanceof BaseMdsRepository)) {
            throw new IllegalStateException(
                    "The target class should extend " + BaseMdsRepository.class.getName()
            );
        }

        ClassLoader webAppClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

        joinPoint.proceed();

        Thread.currentThread().setContextClassLoader(webAppClassLoader);
    }
}

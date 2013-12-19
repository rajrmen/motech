package org.motechproject.commons.couchdb.osgi;

import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.motechproject.commons.couchdb.annotation.DbSetUpStep;
import org.motechproject.commons.couchdb.annotation.PostDbSetUpStep;
import org.motechproject.commons.couchdb.annotation.PreDbSetUpStep;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

public class ApplicationContextTracker extends ServiceTracker {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationContextTracker.class);

    private DbSetUpService dbSetUpService;

    public ApplicationContextTracker(BundleContext context, DbSetUpService dbSetUpService) {
        super(context, ApplicationContext.class.getName(), null);
        this.dbSetUpService = dbSetUpService;
    }

    @Override
    public Object addingService(ServiceReference reference) {
        ApplicationContext applicationContext = (ApplicationContext) super.addingService(reference);
        String bundleName = OsgiStringUtils.nullSafeSymbolicName(reference.getBundle());

        LOG.info(String.format("Application context created for bundle %s ", bundleName));

        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            addPreDbSetUp(applicationContext, beanDefinitionName);
            addDbSetUp(applicationContext, beanDefinitionName);
            addPostDbSetUp(applicationContext, beanDefinitionName);
        }
        return applicationContext;
    }

    private void addPreDbSetUp(ApplicationContext applicationContext, String beanDefinitionName) {
        final Object bean = applicationContext.getBean(beanDefinitionName);
        ReflectionUtils.doWithMethods(bean.getClass(), new ReflectionUtils.MethodCallback() {
                    @Override
                    public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                        dbSetUpService.addPreDbSetUp(new PreDbSetUp(bean, method));
                    }
                }, new ReflectionUtils.MethodFilter() {
                    @Override
                    public boolean matches(Method method) {
                        return method.getAnnotation(PreDbSetUpStep.class) != null;

                    }
                }
        );
    }

    private void addDbSetUp(ApplicationContext applicationContext, String beanDefinitionName) {
        final Object bean = applicationContext.getBean(beanDefinitionName);
        ReflectionUtils.doWithMethods(bean.getClass(), new ReflectionUtils.MethodCallback() {
                    @Override
                    public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                        dbSetUpService.addDbSetUp(new DbSetUp(bean, method));
                    }
                }, new ReflectionUtils.MethodFilter() {
                    @Override
                    public boolean matches(Method method) {
                        return method.getAnnotation(DbSetUpStep.class) != null;

                    }
                }
        );
    }

    private void addPostDbSetUp(final ApplicationContext applicationContext, String beanDefinitionName) {
        final Object bean = applicationContext.getBean(beanDefinitionName);
        ReflectionUtils.doWithMethods(bean.getClass(), new ReflectionUtils.MethodCallback() {
                    @Override
                    public void doWith(Method method) {
                        dbSetUpService.addPostDbSetUp(new PostDbSetUp(bean, method, applicationContext));
                    }
                }, new ReflectionUtils.MethodFilter() {
                    @Override
                    public boolean matches(Method method) {
                        return method.getAnnotation(PostDbSetUpStep.class) != null;

                    }
                }
        );
    }
}

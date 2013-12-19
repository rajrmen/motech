package org.motechproject.commons.couchdb.osgi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PostDbSetUp {

    private static final Logger LOG = LoggerFactory.getLogger(PostDbSetUp.class);

    private final Object bean;
    private final Method method;
    private final ApplicationContext applicationContext;

    public PostDbSetUp(Object bean, Method method, ApplicationContext applicationContext) {

        this.bean = bean;
        this.method = method;
        this.applicationContext = applicationContext;
    }

    public void execute() throws InvocationTargetException, IllegalAccessException {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 0) {
            method.invoke(bean);
        } else if (parameterTypes.length == 1 && parameterTypes[0].equals(ApplicationContext.class)) {
            method.invoke(bean, applicationContext);
        } else {
           LOG.error(String.format("Could not find arguments for method %s on object %s ", method.getName(), bean));
        }
    }

    @Override
    public String toString() {
        return bean.getClass().getName();
    }
}

package org.motechproject.commons.couchdb.osgi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PreDbSetUp {
    private final Object bean;
    private final Method method;

    public PreDbSetUp(Object bean, Method method) {
        this.bean = bean;
        this.method = method;
    }

    public void execute() throws InvocationTargetException, IllegalAccessException {
        method.invoke(bean);
    }
}

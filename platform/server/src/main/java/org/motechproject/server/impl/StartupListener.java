package org.motechproject.server.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class StartupListener implements InvocationHandler {

    private String serviceName;

    public StartupListener(String serviceName) {
        super();
        this.serviceName = serviceName;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
        if (method.getName().equals("handleEvent")) {
            OsgiListener.getOsgiService(serviceName).allowStartup();
            return null;
        } else {
            return method.invoke(proxy, args);
        }
    }
}

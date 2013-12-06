package org.motechproject.commons.couchdb.osgi;

import org.motechproject.commons.couchdb.service.CouchDbManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DbSetUp {

    private Object object;
    private Method method;

    public DbSetUp(Object object, Method method) {
        this.object = object;
        this.method = method;
    }

    public void execute(CouchDbManager couchDbManager) throws InvocationTargetException, IllegalAccessException {
        method.invoke(object, couchDbManager);
    }

    @Override
    public String toString() {
        return object.getClass().getName();
    }
}

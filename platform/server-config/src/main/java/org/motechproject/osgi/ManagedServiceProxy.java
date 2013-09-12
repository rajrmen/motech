package org.motechproject.osgi;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;

public class ManagedServiceProxy implements ManagedService {

    private Method method;
    private Object bean;


    public ManagedServiceProxy(Method method, Object bean) {
        this.method = method;
        this.bean = bean;
    }

    @Override
    public void updated(Dictionary properties) throws ConfigurationException {
        if(properties == null || properties.isEmpty()){
            return;
        }
        ReflectionUtils.invokeMethod(method, bean, toMap(properties));
    }

    private HashMap<String, String> toMap(Dictionary properties) {
        HashMap<String, String> map = new HashMap<>();
        Enumeration keys = properties.keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            map.put(key.toString(), properties.get(key).toString());
        }
        return map;
    }
}

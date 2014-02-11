package org.motechproject.mds.javassist;


import javassist.CtClass;
import org.apache.commons.lang.StringUtils;
import org.osgi.framework.Bundle;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public final class JavassistHelper {

    public static String genericFieldSignature(Class<?> typeClass, Class<?> genericParam) {
        return genericFieldSignature(typeClass.getName(), genericParam.getName());
    }

    public static String genericFieldSignature(String typeClass, String genericParam) {
        return "L" + toClassPath(typeClass) + "<L" + toClassPath(genericParam) + ";>;";
    }

    public static String toClassPath(Class<?> clazz) {
        return toClassPath(clazz.getName());
    }

    public static String toClassPath(String clazz) {
        return StringUtils.replace(clazz, ".", "/") + ".class";
    }

    public static CtClass loadClass(Bundle bundle, String className) throws IOException {
        CtClass clazz = null;

        URL classUrl = bundle.getResource(toClassPath(className));
        if (classUrl != null) {
            try (InputStream classInputStream = classUrl.openStream()) {
                clazz = MotechClassPool.getDefault().makeClass(classInputStream);
            }
        }

        return clazz;
    }

    private JavassistHelper() {
    }
}

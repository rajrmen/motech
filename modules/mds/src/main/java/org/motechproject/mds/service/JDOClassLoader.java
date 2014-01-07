package org.motechproject.mds.service;

public class JDOClassLoader extends ClassLoader {

    public JDOClassLoader() {
        this(JDOClassLoader.class.getClassLoader());
    }

    public JDOClassLoader(ClassLoader parent) {
        super(parent);
    }

    public void defineClass(String className, byte[] classBytes) {
        defineClass(className, classBytes, 0, classBytes.length);
    }
}

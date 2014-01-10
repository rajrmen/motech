package org.motechproject.mds.builder;

public class EntityClassLoader extends ClassLoader {

    public EntityClassLoader(ClassLoader parent) {
        super(parent);
    }

    public void defineClass(String className, byte[] classBytes) {
        defineClass(className, classBytes, 0, classBytes.length);
    }

}

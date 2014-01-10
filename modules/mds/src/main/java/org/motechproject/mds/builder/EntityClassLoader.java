package org.motechproject.mds.builder;

/**
 * The <code>EntityClassLoader</code> class is a wrapper for {@link java.lang.ClassLoader} and it is
 * used by {@link org.motechproject.mds.builder.EntityBuilder} when new entity is built.
 */
public class EntityClassLoader extends ClassLoader {

    public EntityClassLoader(ClassLoader parent) {
        super(parent);
    }

    public void defineClass(String className, byte[] classBytes) {
        defineClass(className, classBytes, 0, classBytes.length);
    }

}

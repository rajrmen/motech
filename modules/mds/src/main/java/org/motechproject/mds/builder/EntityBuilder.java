package org.motechproject.mds.builder;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import org.motechproject.mds.ex.EntityBuilderException;
import org.motechproject.mds.service.JDOClassLoader;

import java.io.IOException;
import java.util.Arrays;

public class EntityBuilder {
    public static final String PACKAGE = "org.motechproject.mds.entity";

    private String className;
    private JDOClassLoader classLoader;
    private byte[] classBytes;

    public EntityBuilder withSingleName(String simpleName) {
        return withClassName(String.format("%s.%s", PACKAGE, simpleName));
    }

    public EntityBuilder withClassName(String className) {
        this.className = className;
        this.classBytes = new byte[0];
        return this;
    }

    public EntityBuilder withClassLoader(JDOClassLoader classLoader) {
        this.classLoader = classLoader;
        this.classBytes = new byte[0];
        return this;
    }

    public String getClassName() {
        return className;
    }

    public byte[] getClassBytes() {
        return Arrays.copyOf(classBytes, classBytes.length);
    }

    public JDOClassLoader getClassLoader() {
        return classLoader;
    }

    public EntityBuilder build() {
        try {
            CtClass ctClass = ClassPool.getDefault().makeClass(className);
            classBytes = ctClass.toBytecode();
            classLoader.defineClass(className, classBytes);
            return this;
        } catch (IOException | CannotCompileException e) {
            throw new EntityBuilderException(e);
        }
    }
}

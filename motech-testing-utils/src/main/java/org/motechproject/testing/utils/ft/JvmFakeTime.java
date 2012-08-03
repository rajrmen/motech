package org.motechproject.testing.utils.ft;

import java.lang.reflect.Method;

public abstract class JvmFakeTime {

    public static void load() {
        try {
            Method m = ClassLoader.class.getDeclaredMethod("loadLibrary", Class.class, String.class, Boolean.TYPE);
            m.setAccessible(true);
            m.invoke(null, System.class, "jvmfaketime", false);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("couldn't load libjvmfaketime.");
        }
    }
}


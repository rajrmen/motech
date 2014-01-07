package org.motechproject.mds.service;

public class BaseMdsService {
    private JDOClassLoader persistenceClassLoader;
    private JDOClassLoader enhancerClassLoader;

    public JDOClassLoader getPersistenceClassLoader() {
        return persistenceClassLoader;
    }

    public void setPersistenceClassLoader(JDOClassLoader persistenceClassLoader) {
        this.persistenceClassLoader = persistenceClassLoader;
    }

    public JDOClassLoader getEnhancerClassLoader() {
        return enhancerClassLoader;
    }

    public void setEnhancerClassLoader(JDOClassLoader enhancerClassLoader) {
        this.enhancerClassLoader = enhancerClassLoader;
    }
}

package org.motechproject.mds.service;

import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

/**
 * The <code>BaseMdsService</code> class is a base class for all services in mds module.
 */
public class BaseMdsService {
    private PersistenceManagerFactory persistenceManagerFactory;
    private SettingsFacade settingsFacade;
    private JDOClassLoader persistenceClassLoader;
    private JDOClassLoader enhancerClassLoader;

    public PersistenceManagerFactory getPersistenceManagerFactory() {
        return persistenceManagerFactory;
    }

    @Autowired
    @Qualifier("persistenceManagerFactory")
    public void setPersistenceManagerFactory(PersistenceManagerFactory persistenceManagerFactory) {
        this.persistenceManagerFactory = persistenceManagerFactory;
    }

    public PersistenceManager getPersistenceManager() {
        return null != persistenceManagerFactory
                ? persistenceManagerFactory.getPersistenceManager()
                : null;
    }

    public SettingsFacade getSettingsFacade() {
        return settingsFacade;
    }

    @Autowired
    public void setSettingsFacade(SettingsFacade settingsFacade) {
        this.settingsFacade = settingsFacade;
    }

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

package org.motechproject.mds;

import org.motechproject.mds.domain.ClassMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

@Component
public class PersistanceClassLoader extends ClassLoader {
    private PersistenceManagerFactory persistenceManagerFactory;

    public PersistanceClassLoader() {
        super(PersistanceClassLoader.class.getClassLoader());
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Query query = getPersistenceManager().newQuery(ClassMapping.class);
        query.setFilter("className == name");
        query.declareParameters("java.lang.String name");
        query.setUnique(true);

        ClassMapping mapping = (ClassMapping) query.execute(name);
        return mapping == null
                ? super.findClass(name)
                : defineClass(name, mapping.getBytecode(), 0, mapping.getLength());
    }

    public void defineClass(ClassMapping mapping) {
        getPersistenceManager().makePersistent(mapping);
    }

    @Autowired
    @Qualifier("persistenceManagerFactory")
    public void setPersistenceManagerFactory(PersistenceManagerFactory persistenceManagerFactory) {
        this.persistenceManagerFactory = persistenceManagerFactory;
    }

    protected PersistenceManager getPersistenceManager() {
        return null != persistenceManagerFactory
                ? persistenceManagerFactory.getPersistenceManager()
                : null;
    }

}

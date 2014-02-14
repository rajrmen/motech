package org.motechproject.mds.service.impl;

import org.motechproject.mds.builder.MDSClassLoader;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.repository.MotechDataRepository;
import org.motechproject.mds.service.MotechDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * This is a basic implementation of {@link org.motechproject.mds.service.MotechDataService}. Mainly
 * it is used as super class to create a service related with the given entity schema in
 * {@link org.motechproject.mds.builder.EntityInfrastructureBuilder} but it can be also used by
 * other services inside this package.
 *
 * @param <T> the type of entity schema.
 */
@Service
public abstract class DefaultMotechDataService<T> implements MotechDataService<T> {
    private MotechDataRepository<T> repository;

    @Override
    @Transactional
    public T create(T object) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader bundleClassLoader = getClass().getClassLoader();
        Thread.currentThread().setContextClassLoader(MDSClassLoader.getInstance());
        T created = repository.create(object);
        Thread.currentThread().setContextClassLoader(classLoader);
        return created;
    }

    @Override
    @Transactional
    public T retrieve(String primaryKeyName, Object value) {
        return repository.retrieve(primaryKeyName, value);
    }

    @Override
    @Transactional
    public List<T> retrieveAll() {
        return repository.retrieveAll();
    }

    @Override
    @Transactional
    public List<T> getAll(int page, int rows) {
        long fromIncl = page * rows - rows +1;
        long toExcl = page * rows;

        return repository.retrieveAll(fromIncl, toExcl);
    }

    @Override
    @Transactional
    public T update(T object) {
        return repository.update(object);
    }

    @Override
    @Transactional
    public void delete(T object) {
        repository.delete(object);
    }

    @Override
    @Transactional
    public void delete(String primaryKeyName, Object value) {
        repository.delete(primaryKeyName, value);
    }

    @Transactional
    public static Class<?> getEntityClass(String entityName) throws ClassNotFoundException {
        String entityClassPath = String.format("%s.%s", Constants.Packages.ENTITY, entityName);
        return getClass(entityClassPath);
    }

    @Transactional
    public static Class<?> getServiceClass(String entityName) throws ClassNotFoundException {
        String serviceClassPath = String.format("%s.%sService", Constants.Packages.SERVICE, entityName);
        return getClass(serviceClassPath);
    }

    @Transactional
    public static Class<?> getServiceImplClass(String entityName) throws ClassNotFoundException {
        String serviceImplClassPath = String.format("%s.%sServiceImpl", Constants.Packages.SERVICE_IMPL, entityName);
        return getClass(serviceImplClassPath);
    }

    @Transactional
    public static Class<?> getRepositoryClass(String entityName) throws ClassNotFoundException {
        String repositoryClassPath = String.format("%s.All%ss", Constants.Packages.REPOSITORY, entityName);
        return getClass(repositoryClassPath);
    }

    @Autowired
    public void setRepository(MotechDataRepository<T> repository) {
        this.repository = repository;
    }

    private static Class<?> getClass(String classPath) throws ClassNotFoundException{
        MDSClassLoader classLoader = MDSClassLoader.getInstance();
        if (null == classLoader.loadClass(classPath)) {
            throw new ClassNotFoundException();
        }
        return classLoader.loadClass(classPath);
    }

    public Class<?> loadClass(String classPath) throws ClassNotFoundException{
        MDSClassLoader classLoader = MDSClassLoader.getInstance();
        if (null == classLoader.loadClass(classPath)) {
            throw new ClassNotFoundException();
        }
        return classLoader.loadClass(classPath);
    }
}

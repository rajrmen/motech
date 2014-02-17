package org.motechproject.mds.service.impl;

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
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            return repository.create(object);
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }

    @Override
    @Transactional
    public T retrieve(String primaryKeyName, Object value) {
        return repository.retrieve(primaryKeyName, value);
    }

    @Override
    @Transactional
    public List<T> retrieveAll() {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            return repository.retrieveAll();
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }

    @Override
    @Transactional
    public List<T> retrieveAll(int page, int rows) {
        long fromIncl = page * rows - rows + 1;
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

    @Autowired
    public void setRepository(MotechDataRepository<T> repository) {
        this.repository = repository;
    }
}

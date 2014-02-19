package org.motechproject.mds.service.impl;

import org.motechproject.mds.repository.MotechDataRepository;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.Order;
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
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            return repository.retrieve(primaryKeyName, value);
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
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
        return retrieveAllImpl(page, rows, null);
    }

    @Override
    @Transactional
    public List<T> retrieveAll(int page, int rows, Order order) {
        return retrieveAllImpl(page, rows, order);
    }

    private List<T> retrieveAllImpl(int page, int rows, Order order) {
        long fromIncl = page * rows - rows;
        long toExcl = page * rows + 1;

        List<T> result;

        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            if (order == null) {
                result =  repository.retrieveAll(fromIncl, toExcl);
            } else {
                result = repository.retrieveAll(fromIncl, toExcl, order);
            }

            return result;
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }

    @Override
    @Transactional
    public T update(T object) {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            return repository.update(object);
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }

    @Override
    @Transactional
    public void delete(T object) {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            repository.delete(object);
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }

    @Override
    @Transactional
    public void delete(String primaryKeyName, Object value) {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            repository.delete(primaryKeyName, value);
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }

    @Override
    @Transactional
    public long count() {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            return repository.count();
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }

    @Autowired
    public void setRepository(MotechDataRepository<T> repository) {
        this.repository = repository;
    }
}

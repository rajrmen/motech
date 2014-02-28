package org.motechproject.mds.service.impl;

import org.apache.commons.beanutils.MethodUtils;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.ex.SecurityException;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.repository.MotechDataRepository;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.QueryParams;
import org.motechproject.mds.util.SecurityMode;
import org.motechproject.security.domain.MotechUserProfile;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;

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
    private AllEntities allEntities;

    private final static String ID = "id";

    @Override
    @Transactional
    public T create(T object) {
        validateCredentials();
        return repository.create(object);
    }

    @Override
    @Transactional
    public T retrieve(String primaryKeyName, Object value) {
        T instance = repository.retrieve(primaryKeyName, value);
        validateCredentials(instance);
        return instance;
    }

    @Override
    @Transactional
    public List<T> retrieveAll() {
        return repository.retrieveAll();
    }

    @Transactional
    protected List<T> retrieveAll(String[] parameters, Object[] values) {
        return repository.retrieveAll(parameters, values);
    }

    @Transactional
    protected List<T> retrieveAll(String[] parameters, Object[] values, QueryParams queryParams) {
        return repository.retrieveAll(parameters, values, queryParams);
    }

    @Transactional
    protected long count(String[] parameters, Object[] values) {
        return repository.count(parameters, values);
    }

    @Override
    @Transactional
    public List<T> retrieveAll(QueryParams queryParams) {
        return repository.retrieveAll(queryParams);
    }

    @Override
    @Transactional
    public T update(T object) {
        try {
            retrieve(ID, MethodUtils.invokeMethod(object, "getId", null));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            LoggerFactory.getLogger(DefaultMotechDataService.class).
                    error("Failed to resolve ID. Instance lacks necessary field.");
        }
        // If retrieve doesn't throw exception, it means the user has got necessary credentials.
        return repository.update(object);
    }

    @Override
    @Transactional
    public void delete(T object) {
        try {
            retrieve(ID, MethodUtils.invokeMethod(object, "getId", null));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            LoggerFactory.getLogger(DefaultMotechDataService.class).
                    error("Failed to resolve ID. Instance lacks necessary field.");
        }
        // If retrieve doesn't throw exception, it means the user has got necessary credentials.
        repository.delete(object);
    }

    @Override
    @Transactional
    public void delete(String primaryKeyName, Object value) {
        T instance = retrieve(primaryKeyName, value);
        repository.delete(instance);
    }

    @Override
    @Transactional
    public long count() {
        return repository.count();
    }

    private void validateCredentials() {
        validateCredentials(null);
    }

    private void validateCredentials(T instance) {
        Class clazz = repository.getClassType();
        Entity entity = allEntities.retrieveByClassName(clazz.getName());
        SecurityMode mode = entity.getSecurityMode();

        boolean authorized = false;

        if (mode.equals(SecurityMode.EVERYONE)) {
            authorized = true;
        } else if (mode.equals(SecurityMode.USERS)) {
            Set<String> users = entity.getSecurityMembers();
            if (users.contains(SecurityContextHolder.getContext().getAuthentication().getName())) {
                authorized = true;
            }
        } else if (mode.equals(SecurityMode.ROLES)) {
            Set<String> roles = entity.getSecurityMembers();
            for (String role : ((MotechUserProfile) SecurityContextHolder.getContext().getAuthentication().getDetails()).getRoles()) {
                if (roles.contains(role)) {
                    authorized = true;
                }
            }
        }

        if (instance != null && !authorized) {
            authorized = hasCredentialsForInstance(instance, mode);
        }

        if (!authorized) {
            throw new SecurityException();
        }
    }

    private boolean hasCredentialsForInstance(T instance, SecurityMode mode) {
        String creator = null, owner = null;
        try {
            creator = (String) MethodUtils.invokeMethod(instance, "getCreator", null);
            owner = (String) MethodUtils.invokeMethod(instance, "getOwner", null);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            LoggerFactory.getLogger(DefaultMotechDataService.class).
                    error("Failed to resolve object creator or owner. Instance lacks necessary fields.");
        }

        if (mode.equals(SecurityMode.OWNER)) {
            return owner.equals(SecurityContextHolder.getContext().getAuthentication().getName());
        } else if (mode.equals(SecurityMode.CREATOR)) {
            return creator.equals(SecurityContextHolder.getContext().getAuthentication().getName());
        }
        return false;
    }

    @Autowired
    public void setRepository(MotechDataRepository<T> repository) {
        this.repository = repository;
    }

    @Autowired
    public void setAllEntities(AllEntities allEntities) {
        this.allEntities = allEntities;
    }
}

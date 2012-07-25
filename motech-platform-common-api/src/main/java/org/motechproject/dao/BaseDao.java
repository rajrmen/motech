package org.motechproject.dao;

import java.util.List;

/**
 *
 */
public interface BaseDao<T extends Object> {

    public void add(T entity);

    public void update(T entity);

    public void remove(T entity);

    public T get(String id);

    public boolean contains(String id);

    public List<T> getAll();

    public void safeRemove(T entity);
}

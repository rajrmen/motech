package org.motechproject.commons.couchdb.dao;

import org.ektorp.BulkDeleteDocument;
import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.impl.NameConventions;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;
import org.motechproject.commons.couchdb.MotechCouchDbRepositorySupport;
import org.motechproject.commons.couchdb.annotation.DbSetUpStep;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;
import org.motechproject.commons.couchdb.service.CouchDbManager;

import java.util.ArrayList;
import java.util.List;

public abstract class MotechBaseRepository<T extends MotechBaseDataObject> {
    private Class<T> type;
    private String dbName;
    private CouchDbRepositorySupport<T> repository;
    protected CouchDbConnector db;
    private String stdDesignDocumentId;

    protected MotechBaseRepository(String dbName, Class<T> type) {
        this.dbName = dbName;
        this.type = type;
        this.stdDesignDocumentId = NameConventions.designDocName(type);
    }

    public T get(String id) {
        return db.get(type, id);
    }

    protected void addOrReplace(T entity, String businessFieldName, String businessId) {
        List<T> entities = entities(businessFieldName, businessId);
        if (entities.size() == 0) {
            repository.add(entity);
        } else if (entities.size() == 1) {
            T entityInDb = entities.get(0);
            entity.setId(entityInDb.getId());
            entity.setRevision(entityInDb.getRevision());
            repository.update(entity);
        } else {
            throw new BusinessIdNotUniqueException(businessFieldName, businessId);
        }
    }

    @DbSetUpStep
    public void setUp(CouchDbManager couchDbManager) {
        this.db = couchDbManager.getConnector(dbName);
        this.repository = new MotechCouchDbRepositorySupport<>(type, db, stdDesignDocumentId, this);
        this.db.createDatabaseIfNotExists();
        this.repository.initStandardDesignDocument();
    }

    private List<T> entities(String businessFieldName, String businessId) {
        String viewName = String.format("by_%s", businessFieldName);
        ViewQuery q = createQuery(viewName).key(businessId).includeDocs(true);
        return db.queryView(q, type);
    }

    public void removeAll(String fieldName, String value) {
        List<T> entities = entities(fieldName, value);
        removeAll(entities);
    }

    private void removeAll(List<T> entities) {
        List<BulkDeleteDocument> bulkDeleteQueue = new ArrayList<BulkDeleteDocument>(entities.size());
        for (T entity : entities) {
            bulkDeleteQueue.add(BulkDeleteDocument.of(entity));
        }
        db.executeBulk(bulkDeleteQueue);
    }

    public void removeAll() {
        removeAll(getAll());
    }

    public void safeRemove(T entity) {
        if (repository.contains(entity.getId())) {
            repository.remove(entity);
        }
    }

    public boolean contains(String id) {
        return repository.contains(id);
    }

    @GenerateView
    public List<T> getAll() {
        return repository.getAll();
    }

    protected List<T> getAll(int limit) {
        ViewQuery q = createQuery("all").limit(limit).includeDocs(true);
        return db.queryView(q, type);
    }

    protected T singleResult(List<T> resultSet) {
        return (resultSet == null || resultSet.isEmpty()) ? null : resultSet.get(0);
    }

    protected List<T> queryView(String viewName, ComplexKey key) {
        return db.queryView(createQuery(viewName)
                .includeDocs(true)
                .key(key),
                type);
    }

    public List<T> queryViewWithKeyList(String viewName, List<String> keys) {
        return db.queryView(createQuery(viewName)
                .includeDocs(true)
                .keys(keys),
                type);
    }

    public List<T> queryView(String viewName) {
        return db.queryView(createQuery(viewName)
                .includeDocs(true),
                type);
    }

    public void add(T entity) {
        repository.add(entity);
    }

    public void update(T entity) {
        repository.update(entity);
    }

    public void remove(T entity) {
        repository.remove(entity);
    }

    protected List<T> queryView(String viewName, String key) {
        return db.queryView(createQuery(viewName)
                .includeDocs(true)
                .key(key),
                type);
    }

    protected List<T> queryView(String viewName, int key) {
        return db.queryView(createQuery(viewName)
                .includeDocs(true)
                .key(key),
                type);
    }

    protected ViewQuery createQuery(String viewName) {
        return new ViewQuery()
                .dbPath(db.path())
                .designDocId(stdDesignDocumentId)
                .viewName(viewName);
    }

    public Class<?> getHandledType() {
        return type;
    }

}

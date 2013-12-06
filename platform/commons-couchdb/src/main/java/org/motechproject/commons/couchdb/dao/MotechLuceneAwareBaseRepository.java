package org.motechproject.commons.couchdb.dao;

import com.github.ldriscoll.ektorplucene.CouchDbRepositorySupportWithLucene;
import com.github.ldriscoll.ektorplucene.LuceneAwareCouchDbConnector;
import org.ektorp.ComplexKey;
import org.ektorp.ViewQuery;
import org.ektorp.http.HttpClient;
import org.ektorp.impl.NameConventions;
import org.ektorp.impl.StdCouchDbInstance;
import org.motechproject.commons.couchdb.MotechCouchDbRepositorySupportWithLucene;
import org.motechproject.commons.couchdb.annotation.DbSetUpStep;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;
import org.motechproject.commons.couchdb.service.CouchDbManager;

import java.io.IOException;
import java.util.List;

public class MotechLuceneAwareBaseRepository<T extends MotechBaseDataObject> {

    protected final String dbName;
    private final Class<T> type;
    private final String stdDesignDocumentId;
    private CouchDbRepositorySupportWithLucene repository;
    protected LuceneAwareCouchDbConnector db;

    protected MotechLuceneAwareBaseRepository(String dbName, Class<T> type) {
        this.dbName = dbName;
        this.type = type;
        this.stdDesignDocumentId = NameConventions.designDocName(type);
    }


    @DbSetUpStep
    public void setUp(CouchDbManager couchDbManager) throws IOException {
        HttpClient connection = couchDbManager.getConnector(dbName).getConnection();
        this.db = new LuceneAwareCouchDbConnector(dbName, new StdCouchDbInstance(connection));
        this.repository = new MotechCouchDbRepositorySupportWithLucene<>(type, db, stdDesignDocumentId, this);
        this.db.createDatabaseIfNotExists();
        this.repository.initStandardDesignDocument();
    }


    public void add(T entity) {
        repository.add(entity);
    }

    public void update(T entity) {
        repository.update(entity);
    }

    public List<T> getAll() {
        return repository.getAll();
    }

    public void remove(T entity) {
        repository.remove(entity);
    }

    protected ViewQuery createQuery(String viewName) {
        return new ViewQuery()
                .dbPath(db.path())
                .designDocId(stdDesignDocumentId)
                .viewName(viewName);
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

    public List<T> queryView(String viewName) {
        return db.queryView(createQuery(viewName)
                .includeDocs(true),
                type);
    }

}

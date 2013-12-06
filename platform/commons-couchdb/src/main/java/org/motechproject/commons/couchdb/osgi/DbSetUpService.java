package org.motechproject.commons.couchdb.osgi;

import org.motechproject.commons.couchdb.service.CouchDbManager;

public interface DbSetUpService {

    void setUpDb(CouchDbManager couchDbManager);

    void preProcess();

    void postProcess();

    void addPreDbSetUp(PreDbSetUp dbSetUp);

    void addDbSetUp(DbSetUp dbSetUp);

    void addPostDbSetUp(PostDbSetUp postDbSetUp);

}

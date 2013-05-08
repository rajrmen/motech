package org.motechproject.couch.mrs.repository.impl;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.couch.mrs.model.CouchMrsUser;
import org.motechproject.couch.mrs.repository.AllCouchMrsUsers;
import org.motechproject.mrs.domain.MRSUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllCouchMrsUsersImpl extends MotechBaseRepository<CouchMrsUser> implements AllCouchMrsUsers  {

    @Autowired
    protected AllCouchMrsUsersImpl(@Qualifier("couchMrsUserDatabaseConnector") CouchDbConnector db) {
        super(CouchMrsUser.class, db);
    }

    @Override
    @View(name = "by_userName", map = "function(doc) { if (doc.type ==='CouchMrsUser') { emit(doc.userName, doc._id); }}")
    public CouchMrsUser getUserByUserName(String userName) {
        List<CouchMrsUser> users = db.queryView(createQuery("by_userName").key(userName).includeDocs(true), CouchMrsUser.class);
        return users != null && users.size() > 0? users.get(0) : null;
    }

    @Override
    public void updateUser(CouchMrsUser mrsUser) {
        addOrReplace(mrsUser, "userName", mrsUser.getUserName());
    }

    @Override
    public List<? extends MRSUser> getAllUsers() {
        return getAll();
    }
}

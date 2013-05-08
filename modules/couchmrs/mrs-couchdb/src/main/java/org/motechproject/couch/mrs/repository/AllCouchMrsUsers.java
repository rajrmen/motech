package org.motechproject.couch.mrs.repository;

import org.motechproject.couch.mrs.model.CouchMrsUser;

public interface AllCouchMrsUsers {

    CouchMrsUser getUserByUserName(String userName);

    void saveUser(CouchMrsUser mrsUser);
}

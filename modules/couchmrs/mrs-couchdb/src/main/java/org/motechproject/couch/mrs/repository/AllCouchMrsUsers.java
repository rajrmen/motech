package org.motechproject.couch.mrs.repository;

import org.motechproject.couch.mrs.model.CouchMrsUser;
import org.motechproject.mrs.domain.MRSUser;

import java.util.List;

public interface AllCouchMrsUsers {

    List<? extends MRSUser> getAllUsers();

    CouchMrsUser getUserByUserName(String userName);

    void addOrReplace(CouchMrsUser mrsUser);
}

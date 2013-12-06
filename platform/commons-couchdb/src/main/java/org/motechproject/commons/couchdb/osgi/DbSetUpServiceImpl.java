package org.motechproject.commons.couchdb.osgi;

import org.motechproject.commons.couchdb.service.CouchDbManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DbSetUpServiceImpl implements DbSetUpService {


    private static final Logger LOGGER = LoggerFactory.getLogger(DbSetUpService.class);

    private List<PreDbSetUp> preDbSetUpList = Collections.synchronizedList(new ArrayList<PreDbSetUp>());
    private List<DbSetUp> dbSetUpList = Collections.synchronizedList(new ArrayList<DbSetUp>());
    private List<PostDbSetUp> postDbSetUpList = Collections.synchronizedList(new ArrayList<PostDbSetUp>());


    @Override
    public void preProcess() {
        for (PreDbSetUp preDbSetUp : preDbSetUpList) {
            try {
                preDbSetUp.execute();
            } catch (Exception ex) {
                LOGGER.error(String.format("Could not complete post processing after design document creation %s", preDbSetUp), ex);
            }
        }
    }

    @Override
    public void setUpDb(CouchDbManager couchDbManager) {
        for (DbSetUp dbSetUp : dbSetUpList) {
            try {
                dbSetUp.execute(couchDbManager);
            } catch (InvocationTargetException | IllegalAccessException ex) {
                LOGGER.error(String.format("Could not create design documents for %s", dbSetUp), ex);
            }
        }
    }

    @Override
    public void postProcess() {
        for (PostDbSetUp postDbSetUp : postDbSetUpList) {
            try {
                postDbSetUp.execute();
            } catch (InvocationTargetException | IllegalAccessException ex) {
                LOGGER.error(String.format("Could not complete post processing after design document creation %s", postDbSetUp), ex);
            }
        }
    }

    @Override
    public void addPreDbSetUp(PreDbSetUp preDbSetUp) {
        preDbSetUpList.add(preDbSetUp);
    }

    @Override
    public void addDbSetUp(DbSetUp dbSetUp) {
        dbSetUpList.add(dbSetUp);
    }

    @Override
    public void addPostDbSetUp(PostDbSetUp postDbSetUp) {
        postDbSetUpList.add(postDbSetUp);
    }
}

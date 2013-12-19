package org.motechproject.commons.couchdb.dao;

import org.ektorp.CouchDbConnector;
import org.ektorp.UpdateConflictException;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.DesignDocument;
import org.ektorp.support.DesignDocumentFactory;
import org.ektorp.support.StdDesignDocumentFactory;
import org.motechproject.commons.couchdb.dao.MotechViewGenerator;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

import java.util.Random;

public class MotechCouchDbRepositorySupport<T extends MotechBaseDataObject> extends CouchDbRepositorySupport<T> {

    private final String designDocumentId;
    private final MotechBaseRepository<T> viewMetaDataSource;

    public MotechCouchDbRepositorySupport(Class<T> type, CouchDbConnector db, String designDocumentId, MotechBaseRepository<T> viewMetaDataSource) {
        super(type, db);
        this.designDocumentId = designDocumentId;
        this.viewMetaDataSource = viewMetaDataSource;
        StdDesignDocumentFactory designDocumentFactory = new StdDesignDocumentFactory();
        designDocumentFactory.viewGenerator = new MotechViewGenerator();
        setDesignDocumentFactory(designDocumentFactory);
    }

    @Override
    public void initStandardDesignDocument() {
        initDesignDocInternal(0);
    }

    private void initDesignDocInternal(int invocations) {
        DesignDocument designDoc;
        if (db.contains(designDocumentId)) {
            designDoc = getDesignDocumentFactory().getFromDatabase(db, designDocumentId);
        } else {
            designDoc = getDesignDocumentFactory().newDesignDocumentInstance();
            designDoc.setId(designDocumentId);
        }
        log.debug("Generating DesignDocument for {}", getHandledType());
        DesignDocumentFactory designDocumentFactory = getDesignDocumentFactory();
        DesignDocument generated = designDocumentFactory.generateFrom(viewMetaDataSource);
        boolean changed = designDoc.mergeWith(generated);
        if (log.isDebugEnabled()) {
            debugDesignDoc(designDoc);
        }
        if (changed) {
            log.debug("DesignDocument changed or new. Updating database");
            try {
                db.update(designDoc);
            } catch (UpdateConflictException e) {
                log.warn("Update conflict occurred when trying to update design document: {}", designDoc.getId());
                if (invocations == 0) {
                    backOff();
                    log.info("retrying initStandardDesignDocument for design document: {}", designDoc.getId());
                    initDesignDocInternal(1);
                }
            }
        } else if (log.isDebugEnabled()) {
            log.debug("DesignDocument was unchanged. Database was not updated.");
        }
    }

    private void backOff() {
        try {
            Thread.sleep(new Random().nextInt(400));
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            return;
        }
    }

    Class<?> getHandledType() {
        return type;
    }
}

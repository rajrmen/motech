package org.motechproject.commons.couchdb.dao;

import com.github.ldriscoll.ektorplucene.CouchDbRepositorySupportWithLucene;
import com.github.ldriscoll.ektorplucene.LuceneAwareCouchDbConnector;
import com.github.ldriscoll.ektorplucene.designdocument.LuceneDesignDocument;
import org.ektorp.UpdateConflictException;
import org.ektorp.support.DesignDocument;
import org.ektorp.support.StdDesignDocumentFactory;
import org.motechproject.commons.couchdb.dao.MotechViewGenerator;
import org.motechproject.commons.couchdb.dao.MotechLuceneAwareBaseRepository;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

import java.util.Random;

public class MotechCouchDbRepositorySupportWithLucene<T extends MotechBaseDataObject> extends CouchDbRepositorySupportWithLucene<T> {

    private final String designDocumentId;
    private final MotechLuceneAwareBaseRepository<T> viewMetaDataSource;

    public MotechCouchDbRepositorySupportWithLucene(Class<T> type, LuceneAwareCouchDbConnector db, String designDocumentId, MotechLuceneAwareBaseRepository<T> viewMetaDataSource) {
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
        LuceneDesignDocument designDoc;
        if (db.contains(stdDesignDocumentId)) {
            designDoc = db.get(LuceneDesignDocument.class, designDocumentId);
        } else {
            designDoc = new LuceneDesignDocument(designDocumentId);
        }
        log.debug("Generating DesignDocument for {}", type);
        DesignDocument generated = getDesignDocumentFactory().generateFrom(viewMetaDataSource);
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
        } else {
            log.debug("DesignDocument was unchanged. Database was not updated.");
        }
    }

    private void backOff() {
        try {
            Thread.sleep(new Random().nextInt(400));
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
}

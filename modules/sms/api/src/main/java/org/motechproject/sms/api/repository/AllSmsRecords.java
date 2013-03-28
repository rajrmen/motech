package org.motechproject.sms.api.repository;

import com.github.ldriscoll.ektorplucene.CouchDbRepositorySupportWithLucene;
import com.github.ldriscoll.ektorplucene.CustomLuceneResult;
import com.github.ldriscoll.ektorplucene.LuceneAwareCouchDbConnector;
import com.github.ldriscoll.ektorplucene.LuceneQuery;
import com.github.ldriscoll.ektorplucene.designdocument.annotation.FullText;
import com.github.ldriscoll.ektorplucene.designdocument.annotation.Index;
import org.codehaus.jackson.type.TypeReference;
import org.ektorp.CouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.motechproject.commons.couchdb.lucene.query.CouchDbLuceneQuery;
import org.motechproject.sms.api.DeliveryStatus;
import org.motechproject.sms.api.domain.SmsRecord;
import org.motechproject.sms.api.service.SmsRecordSearchCriteria;
import org.motechproject.sms.api.web.SmsRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.sort;
import static java.util.Collections.reverseOrder;
import static org.apache.commons.lang.StringUtils.isNotBlank;

@Repository
public class AllSmsRecords extends CouchDbRepositorySupportWithLucene<SmsRecord> {

    public void updateDeliveryStatus(String recipient, String referenceNumber, String deliveryStatus) {
        SmsRecord smsRecord = findLatestBy(recipient, referenceNumber);
        if (smsRecord != null) {
            smsRecord.setStatus(DeliveryStatus.valueOf(deliveryStatus));
            update(smsRecord);
        }
    }

    SmsRecord findLatestBy(String recipient, String referenceNumber) {
        SmsRecords smsRecords = findAllBy(new SmsRecordSearchCriteria()
                .withPhoneNumber(recipient)
                .withReferenceNumber(referenceNumber), 0, 100, null, false);
        return CollectionUtils.isEmpty(smsRecords.getRecords()) ? null : (SmsRecord) sort(smsRecords.getRecords(), on(SmsRecord.class).getMessageTime(), reverseOrder()).get(0);
    }

    public void addOrReplace(SmsRecord smsRecord) {
        SmsRecords smsRecordsInDb = findAllBy(new SmsRecordSearchCriteria()
                .withPhoneNumber(smsRecord.getPhoneNumber())
                .withMessageTime(smsRecord.getMessageTime())
                .withReferenceNumber(smsRecord.getReferenceNumber()), 0, 100, null, false);

        if (CollectionUtils.isEmpty(smsRecordsInDb.getRecords())) {
            add(smsRecord);
        } else {
            SmsRecord smsRecordInDb = smsRecordsInDb.getRecords().get(0);
            smsRecord.setId(smsRecordInDb.getId());
            smsRecord.setRevision(smsRecordInDb.getRevision());
            update(smsRecord);
        }
    }

    @FullText({@Index(
            name = "search",
            index = "function(doc) { " +
                    "var result=new Document(); " +
                    "result.add(doc.smsType,{'field':'smsType'}); " +
                    "result.add(doc.phoneNumber, {'field':'phoneNumber'});" +
                    "result.add(doc.messageContent, {'field':'messageContent'}); " +
                    "result.add(doc.messageTime,{'field':'messageTime', 'type':'date'}); " +
                    "result.add(doc.deliveryStatus, {'field':'deliveryStatus'}); " +
                    "result.add(doc.referenceNumber, {'field':'referenceNumber'}); " +
                    "return result " +
                    "}"
    )})
    public SmsRecords findAllBy(SmsRecordSearchCriteria criteria, int page, int pageSize, String sortBy, boolean reverse) {
        StringBuilder query = new CouchDbLuceneQuery()
                .withAny("smsType", criteria.getSmsTypes())
                .with("phoneNumber", criteria.getPhoneNumber())
                .with("messageContent", criteria.getMessageContent())
                .withDateRange("messageTime", criteria.getMessageTimeRange())
                .withAny("deliveryStatus", criteria.getDeliveryStatuses())
                .with("referenceNumber", criteria.getReferenceNumber())
                .build();
        return runQuery(query, 0, 100, null, false);
    }

    private SmsRecords runQuery(StringBuilder queryString, int page, int pageSize, String sortBy, boolean reverse) {
        LuceneQuery query = new LuceneQuery("SmsRecord", "search");
        query.setQuery(queryString.toString());
        query.setStaleOk(false);
        query.setIncludeDocs(true);
        if (pageSize > 0) {
            query.setLimit(pageSize);
            query.setSkip(page * pageSize);
        }
        if (isNotBlank(sortBy)) {
            String sortString = reverse ? "\\" + sortBy : sortBy;
            query.setSort(sortString);
        }
        TypeReference<CustomLuceneResult<SmsRecord>> typeRef
                = new TypeReference<CustomLuceneResult<SmsRecord>>() {
        };
        return convertToSmsRecords(db.queryLucene(query, typeRef));
    }

    private SmsRecords convertToSmsRecords(CustomLuceneResult<SmsRecord> result) {
        List<SmsRecord> smsRecords = new ArrayList<>();
        int count = 0;
        if (result != null) {
            List<CustomLuceneResult.Row<SmsRecord>> rows = result.getRows();
            for (CustomLuceneResult.Row<SmsRecord> row : rows) {
                smsRecords.add(row.getDoc());
            }
            count = result.getTotalRows();
        }
        return new SmsRecords(count, smsRecords);
    }

    //TODO: Create Base class and move it to there.
    public void removeAll() {
        for (SmsRecord smsRecord : getAll()) {
            remove(smsRecord);
        }
    }

    @Autowired
    protected AllSmsRecords(@Qualifier("smsDBConnector") CouchDbConnector db) throws IOException {
        super(SmsRecord.class, new LuceneAwareCouchDbConnector(db.getDatabaseName(), new StdCouchDbInstance(db.getConnection())));
        initStandardDesignDocument();
    }
}

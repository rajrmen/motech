package org.motechproject.email.repository;

import com.github.ldriscoll.ektorplucene.CustomLuceneResult;
import com.github.ldriscoll.ektorplucene.LuceneQuery;
import com.github.ldriscoll.ektorplucene.designdocument.annotation.FullText;
import com.github.ldriscoll.ektorplucene.designdocument.annotation.Index;
import org.codehaus.jackson.type.TypeReference;
import org.joda.time.DateTime;
import org.motechproject.commons.couchdb.lucene.query.CouchDbLuceneQuery;
import org.motechproject.commons.couchdb.query.QueryParam;
import org.motechproject.email.domain.EmailRecords;
import org.motechproject.email.service.EmailRecordSearchCriteria;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import com.github.ldriscoll.ektorplucene.CouchDbRepositorySupportWithLucene;
import com.github.ldriscoll.ektorplucene.LuceneAwareCouchDbConnector;
import org.ektorp.CouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.motechproject.email.domain.EmailRecord;
import org.springframework.util.CollectionUtils;

import static java.util.Collections.reverseOrder;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.sort;

@Repository
public class AllEmailRecords extends CouchDbRepositorySupportWithLucene<EmailRecord> {

    private final Logger logger = LoggerFactory.getLogger(AllEmailRecords.class);

    public void addOrReplace(EmailRecord emailRecord) {
        EmailRecords emailRecordsInDb = findAllBy(new EmailRecordSearchCriteria()
                .withToAddress(emailRecord.getToAddress())
                .withMessageTime(emailRecord.getDeliveryTime())
                .withReferenceNumber(emailRecord.getReferenceNumber()));
        if (CollectionUtils.isEmpty(emailRecordsInDb.getRecords())) {
            add(emailRecord);
        } else {
            EmailRecord emailRecordInDb = emailRecordsInDb.getRecords().get(0);
            emailRecord.setId(emailRecordInDb.getId());
            emailRecord.setRevision(emailRecordInDb.getRevision());
            update(emailRecord);
        }
    }

    EmailRecord findLatestBy(String toAddress, String referenceNumber) {
        EmailRecords emailRecords = findAllBy(new EmailRecordSearchCriteria()
                .withToAddress(toAddress)
                .withReferenceNumber(referenceNumber));
        return CollectionUtils.isEmpty(emailRecords.getRecords()) ? null : (EmailRecord) sort(emailRecords.getRecords(), on(EmailRecord.class).getDeliveryTime(), reverseOrder()).get(0);
    }

    @FullText({@Index(
            name = "search",
            index = "function(doc) { " +
                    "var result=new Document(); " +
                    "result.add(doc.fromAddress, {'field':'fromAddress'});" +
                    "result.add(doc.toAddress, {'field':'toAddress'}); " +
                    "result.add(doc.subject, {'field':'subject'}); " +
                    "result.add(doc.message, {'field':'message'}); " +
                    "result.add(doc.deliveryTime,{'field':'deliveryTime', 'type':'date'}); " +
                    "result.add(doc.deliveryStatus, {'field':'deliveryStatus'}); " +
                    "result.add(doc.referenceNumber, {'field':'referenceNumber'}); " +
                    "return result " +
                    "}"
    )})
    public EmailRecords findAllBy(EmailRecordSearchCriteria criteria) {
        StringBuilder query = new CouchDbLuceneQuery()
                .with("fromAddress", criteria.getFromAddress())
                .with("toAddress", criteria.getToAddress())
                .with("subject", criteria.getSubject())
                .with("message", criteria.getMessage())
                .withDateRange("deliveryTime", criteria.getDeliveryTimeRange())
                .withAny("deliveryStatus", criteria.getDeliveryStatuses())
                .with("referenceNumber", criteria.getReferenceNumber())
                .build();
        return runQuery(query, criteria.getQueryParam());
    }

    private EmailRecords runQuery(StringBuilder queryString, QueryParam queryParam) {
        LuceneQuery query = new LuceneQuery("EmailRecord", "search");
        query.setQuery(queryString.toString());
        query.setStaleOk(false);
        query.setIncludeDocs(true);
        int recordsPerPage = queryParam.getRecordsPerPage();
        if (recordsPerPage > 0) {
            query.setLimit(recordsPerPage);
            query.setSkip(queryParam.getPageNumber() * recordsPerPage);
        }
        String sortBy = queryParam.getSortBy();
        if (isNotBlank(sortBy)) {
            Class clazz = EmailRecord.class;
            try {
                Field f = clazz.getDeclaredField(sortBy);
                if (f.getType().equals(DateTime.class)) {
                    sortBy = sortBy + "<date>";
                }
            } catch (NoSuchFieldException e) {
                logger.error(String.format("No found field %s", sortBy), e);
            }
            String sortString = queryParam.isReverse() ? "\\" + sortBy : sortBy;
            query.setSort(sortString);
        }
        TypeReference<CustomLuceneResult<EmailRecord>> typeRef
                = new TypeReference<CustomLuceneResult<EmailRecord>>() {
        };
        return convertToEmailRecords(db.queryLucene(query, typeRef));
    }

    private EmailRecords convertToEmailRecords(CustomLuceneResult<EmailRecord> result) {
        List<EmailRecord> smsRecords = new ArrayList<>();
        int count = 0;
        if (result != null) {
            List<CustomLuceneResult.Row<EmailRecord>> rows = result.getRows();
            for (CustomLuceneResult.Row<EmailRecord> row : rows) {
                smsRecords.add(row.getDoc());
            }
            count = result.getTotalRows();
        }
        return new EmailRecords(count, smsRecords);
    }

    public void removeAll() {
        for (EmailRecord emailRecord : getAll()) {
            remove(emailRecord);
        }
    }

    @Autowired
    protected AllEmailRecords(@Qualifier("emailDBConnector") CouchDbConnector db) throws IOException {
        super(EmailRecord.class, new LuceneAwareCouchDbConnector(db.getDatabaseName(), new StdCouchDbInstance(db.getConnection())));
        initStandardDesignDocument();
    }
}

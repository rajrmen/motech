package org.motechproject.adherence.dao;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.support.View;
import org.joda.time.LocalDate;
import org.motechproject.adherence.domain.AdherenceLog;
import org.motechproject.adherence.domain.Concept;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class AllAdherenceLogs extends MotechBaseRepository<AdherenceLog> {

    @Autowired
    protected AllAdherenceLogs(@Qualifier("adherenceDbConnector") CouchDbConnector db) {
        super(AdherenceLog.class, db);
    }

    public void insert(AdherenceLog entity) {
        AdherenceLog latestLog = findLatestLog(entity.getExternalId(), entity.getConcept());
        if (latestLog == null) {
            add(entity);
        } else if (latestLog.overlaps(entity)) {
            entity = latestLog.cut(entity);
            add(entity);
        } else if (!latestLog.encloses(entity)) {
            add(entity);
        }
    }

    @View(name = "find_by_date", map = "function(doc) {if(doc.type == 'AdherenceLog') {emit([doc.externalId, doc.concept, doc.toDate], doc._id);} }")
    public AdherenceLog findByDate(String externalId, Concept concept, LocalDate date) {
        ViewQuery query = createQuery("find_by_date").startKey(ComplexKey.of(externalId, concept, date)).limit(1).includeDocs(true);
        List<AdherenceLog> adherenceLogs = db.queryView(query, AdherenceLog.class);
        if (CollectionUtils.isEmpty(adherenceLogs)) {
            return null;
        } else {
            AdherenceLog adherenceLog = adherenceLogs.get(0);
            if (!adherenceLog.getFromDate().isAfter(date)) {
                return adherenceLog;
            }
            return null;
        }
    }

    public AdherenceLog findLatestLog(String externalId, Concept concept) {
        ViewQuery query = createQuery("find_by_date").startKey(ComplexKey.of(externalId, concept, ComplexKey.emptyObject())).limit(1).descending(true).includeDocs(true);
        List<AdherenceLog> adherenceLogs = db.queryView(query, AdherenceLog.class);
        return CollectionUtils.isEmpty(adherenceLogs) ? null : adherenceLogs.get(0);
    }

    @View(
            name = "find_all_between_date_range",
            map = "function(doc) {" +
                    "if(doc.type == 'AdherenceLog') {" +
                    "emit([doc.externalId, doc.concept, doc.fromDate], doc);" +
                    "emit([doc.externalId, doc.concept, doc.toDate], doc);" +
                    "} " +
                    "}",
            reduce =
                    " function uniqueArrayValues(o){" +
                            "  var items = o," +
                            "  ids=[];" +
                            "  output = [];" +
                            "  function check(val){" +
                            "    return ids.indexOf(val._id) === -1;" +
                            "  }" +
                            "  for(var i=0; i<items.length; i++){" +
                            "    if(check(items[i])){" +
                            "     output.push(items[i]);" +
                            "     ids.push(items[i]._id);" +
                            "    }    " +
                            "  }" +
                            "  return output;" +
                            "}" +
                            "function(keys,values){" +
                            "  values=uniqueArrayValues(values);" +
                            "  for(var j in values){" +
                            "    var summary={};" +
                            "    summary.deltaDosesTaken=values[j].deltaDosesTaken;" +
                            "    summary.deltaTotalDoses=values[j].deltaTotalDoses;" +
                            "    summary._id=values[j]._id;" +
                            "    values[j]=summary;" +
                            "  }" +
                            "  return values;" +
                            "}"
    )
    public List<AdherenceRecord> getDeltaCounts(String externalId, Concept concept, LocalDate startDate, LocalDate endDate) {
        try {
            ViewQuery query = createQuery("find_all_between_date_range").startKey(ComplexKey.of(externalId, concept, startDate)).endKey(ComplexKey.of(externalId, concept, endDate)).groupLevel(1).includeDocs(false).reduce(true).inclusiveEnd(true);
            return parse(query);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private List<AdherenceRecord> parse(ViewQuery query) throws IOException {
        ViewResult viewResult = db.queryView(query);
        List<AdherenceRecord> records = new ArrayList<AdherenceRecord>();
        ObjectMapper mapper = new ObjectMapper();
        for (ViewResult.Row row : viewResult.getRows()) {
            JsonNode values = row.getValueAsNode();
            for (int i = 0; i < values.size(); i++) {
                records.add(mapper.readValue(values.get(i), AdherenceRecord.class));
            }
        }
        return records;
    }
}

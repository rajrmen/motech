package org.motechproject.osgi.web.repository;

import org.ektorp.support.View;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.osgi.web.domain.LogMapping;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * The <code>AllLogMappings</code> class is used for getting information about loggers from database.
 */

@Repository
public class AllLogMappings extends MotechBaseRepository<LogMapping> {


    public AllLogMappings() {
        super("motech-loggers", LogMapping.class);
    }

    @View(name = "by_logName", map = "function(doc) { if(doc.type === 'LogMapping') emit(doc.logName); }")
    public LogMapping byLogName(String logName) {
        List<LogMapping> result = queryView("by_logName", logName);
        return (result.isEmpty() ? null : result.get(0));
    }

    public void addOrUpdate(LogMapping mapping) {
        LogMapping existing = byLogName(mapping.getLogName());

        if (existing == null) {
            add(mapping);
        } else {
            existing.setLogName(mapping.getLogName());
            existing.setLogLevel(mapping.getLogLevel());
            update(existing);
        }
    }

    public void removeByLogName(String name) {
        LogMapping mapping = byLogName(name);

        if (mapping != null) {
            remove(mapping);
        }
    }
}

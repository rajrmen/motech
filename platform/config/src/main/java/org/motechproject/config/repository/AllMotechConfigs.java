package org.motechproject.config.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewResult;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.config.domain.MotechConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllMotechConfigs extends MotechBaseRepository<MotechConfig> {

    @View(name = "by_key_and_module", map = "function(doc) { if(doc.type === 'MotechConfig') { " +
            "for (config in doc.configs) {" +
            "  emit([config, doc.module], doc.configs[config]);" +
            "}}}")
    public String findConfig(String key, String module) {
        ViewResult result = db.queryView(createQuery("by_key_and_module").key(ComplexKey.of(key, module)).includeDocs(false));
        for (ViewResult.Row row : result) {
            return row.getValue();
        }
        return null;
    }

    public void addOrReplace(MotechConfig config) {
        super.addOrReplace(config, "module", config.getModule());
    }

    @GenerateView
    protected MotechConfig findByModule(String module) {
        return singleResult(queryView("by_module", module));
    }

    @Autowired
    public AllMotechConfigs(@Qualifier("motechConfigDbConnector") CouchDbConnector connector) {
        super(MotechConfig.class, connector);
    }
}

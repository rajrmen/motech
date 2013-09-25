package org.motechproject.security.repository;

import java.util.Collections;
import java.util.List;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.security.domain.MotechSecurityConfiguration;
import org.motechproject.security.domain.MotechURLSecurityRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@View(name = "all", map = "function(doc) { emit(doc._id, doc); }")
public class AllMotechSecurityRulesCouchdbImpl extends MotechBaseRepository<MotechSecurityConfiguration> implements AllMotechSecurityRules {

    @Autowired
    protected AllMotechSecurityRulesCouchdbImpl(@Qualifier("webSecurityDbConnector") CouchDbConnector db) {
        super(MotechSecurityConfiguration.class, db);
        initStandardDesignDocument();
    }

    @Override
    public void add(MotechSecurityConfiguration config) {
        if (this.getAll().size() == 0) {
            super.add(config);
        } else {
            MotechSecurityConfiguration oldConfig = this.getAll().get(0);
            config.setRevision(oldConfig.getRevision());
            config.setId(oldConfig.getId());
            super.update(config);
        }
    }

    @Override
    public void update(MotechSecurityConfiguration config) {
        super.update(config);
    }

    @Override
    public void remove(MotechSecurityConfiguration config) {
        super.remove(config);
    }

    @Override
    public List<MotechURLSecurityRule> getRules() {
        List<MotechSecurityConfiguration> config = this.getAll();

        return config.size() == 0 ? Collections.<MotechURLSecurityRule>emptyList() : config.get(0).getSecurityRules();
    }
}

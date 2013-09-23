package org.motechproject.security.repository;

import java.util.ArrayList;
import java.util.List;
import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.security.domain.MotechURLSecurityRule;
import org.motechproject.security.domain.MotechURLSecurityRuleCouchdbImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@View(name = "all", map = "function(doc) { emit(doc._id, doc); }")
public class AllMotechSecurityRulesCouchdbImpl extends MotechBaseRepository<MotechURLSecurityRuleCouchdbImpl> implements AllMotechSecurityRules {

    @Autowired
    protected AllMotechSecurityRulesCouchdbImpl(@Qualifier("webSecurityDbConnector") CouchDbConnector db) {
        super(MotechURLSecurityRuleCouchdbImpl.class, db);
        initStandardDesignDocument();
    }

    @Override
    public void add(MotechURLSecurityRule rule) {
        super.add((MotechURLSecurityRuleCouchdbImpl) rule);
    }

    @Override
    public void update(MotechURLSecurityRule rule) {
        super.update((MotechURLSecurityRuleCouchdbImpl) rule);
    }

    @Override
    public void remove(MotechURLSecurityRule rule) {
        super.remove((MotechURLSecurityRuleCouchdbImpl) rule);
    }

    @Override
    public List<MotechURLSecurityRule> getRules() {
        return new ArrayList<MotechURLSecurityRule>(getAll());
    }

    @Override
    @View(name = "by_module_and_pattern", map = "function(doc) { if (doc.type ==='MotechSecurityRule') { emit([doc.module, doc.pattern], doc._id); }}")
    public MotechURLSecurityRule findByModuleAndPattern(String module, String pattern) {
        if (module == null || pattern == null) { return null; }
        ViewQuery viewQuery = createQuery("by_module_and_pattern").key(ComplexKey.of(module, pattern));
        return singleResult(db.queryView(viewQuery, MotechURLSecurityRuleCouchdbImpl.class));
    }

    @Override
    @View(name = "by_module", map = "function(doc) { if (doc.type ==='MotechSecurityRule') { emit(doc.module, doc._id); }}")
    public List<MotechURLSecurityRule> findByModule(String module) {
        if (module == null) { return null; }
        ViewQuery viewQuery = createQuery("by_module").key(module).includeDocs(true);
        return buildList(db.queryView(viewQuery, MotechURLSecurityRuleCouchdbImpl.class));
    }

    private List<MotechURLSecurityRule> buildList(List<MotechURLSecurityRuleCouchdbImpl> queryResults) {
        List<MotechURLSecurityRule> securityRules = new ArrayList<MotechURLSecurityRule>();

        for (MotechURLSecurityRuleCouchdbImpl rule : queryResults) {
            securityRules.add(rule);
        }

        return securityRules;
    }

}

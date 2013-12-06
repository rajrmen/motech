package org.motechproject.event.aggregation.repository;

import org.ektorp.support.GenerateView;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.event.aggregation.model.rule.AggregationRuleRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AllAggregationRules extends MotechBaseRepository<AggregationRuleRecord> {


    public AllAggregationRules() {
        super("motech-event-aggregation", AggregationRuleRecord.class);
    }

    @GenerateView
    public AggregationRuleRecord findByName(String name) {
        return singleResult(queryView("by_name", name));
    }

    public void addOrReplace(AggregationRuleRecord rule) {
        super.addOrReplace(rule, "name", rule.getName());
    }

    public void remove(String ruleName) {
        remove(findByName(ruleName));
    }
}

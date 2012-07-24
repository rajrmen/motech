package org.motechproject.decisiontree.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.decisiontree.domain.TreeDao;
import org.motechproject.decisiontree.model.Tree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllTrees extends MotechBaseRepository<TreeDao> {
    @Autowired
    protected AllTrees(@Qualifier("treesDatabase") CouchDbConnector db) {
        super(TreeDao.class, db);
    }

    @GenerateView
    public TreeDao findByName(String name) {
        return singleResult(queryView("by_name", name));
    }

    public void addOrReplace(TreeDao entity) {
        super.addOrReplace(entity, "name", entity.name());
    }
}

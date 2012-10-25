package org.motechproject.security.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.security.domain.MotechRole;
import org.motechproject.security.domain.MotechRoleCouchdbImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@View(name = "all", map = "function(doc) { emit(doc._id, doc); }")
public class AllMotechRolesCouchdbImpl extends MotechBaseRepository<MotechRoleCouchdbImpl> implements AllMotechRoles {

    @Autowired
    protected AllMotechRolesCouchdbImpl(@Qualifier("webSecurityDbConnector") CouchDbConnector db) {
        super(MotechRoleCouchdbImpl.class, db);
        initStandardDesignDocument();
    }

    @Override
    public void add(MotechRole role) {
        if (findByRoleName(role.getRoleName()) != null) { return; }
        super.add((MotechRoleCouchdbImpl) role);
    }

    @Override
    @View(name = "by_roleName", map = "function(doc) { if (doc.type ==='MotechRole') { emit(doc.roleName, doc._id); }}")
    public MotechRole findByRoleName(String roleName) {
        if (roleName == null) { return null; }

        String lowerUserName = roleName.toLowerCase();
        ViewQuery viewQuery = createQuery("by_roleName").key(lowerUserName).includeDocs(true);
        return singleResult(db.queryView(viewQuery, MotechRoleCouchdbImpl.class));
    }


}

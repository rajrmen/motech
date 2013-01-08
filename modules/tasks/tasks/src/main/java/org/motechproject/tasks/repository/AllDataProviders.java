package org.motechproject.tasks.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.tasks.domain.DataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@View(name = "by_name", map = "function(doc) { if(doc.type === 'DataProvider') emit(doc.name); }")
public class AllDataProviders extends MotechBaseRepository<DataProvider> {

    @Autowired
    public AllDataProviders(final CouchDbConnector connector) {
        super(DataProvider.class, connector);
    }

    public void addOrUpdate(DataProvider entity) {
        DataProvider provider = byName(entity.getName());

        if (provider != null) {
            provider.setName(entity.getName());
            provider.setObjects(entity.getObjects());

            update(provider);
        } else {
            add(entity);
        }
    }

    public DataProvider byName(String name) {
        List<DataProvider> providers = queryView("by_name", name);
        return providers.isEmpty() ? null : providers.get(0);
    }
}

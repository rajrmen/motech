package org.motechproject.tasks.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.dao.BusinessIdNotUniqueException;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.tasks.domain.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AllTasks extends MotechBaseRepository<Task> {

    @Autowired
    public AllTasks(final CouchDbConnector connector) {
        super(Task.class, connector);
    }

    public void addOrUpdate(Task task) throws BusinessIdNotUniqueException{
        addOrReplace(task, "id", task.getId());
    }

}

package org.motechproject.tasks.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.tasks.domain.TaskError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@View(name = "by_taskId", map = "function(doc) { if(doc.type === 'Channel') emit(doc.taskId); }")
public class AllTaskErrors extends MotechBaseRepository<TaskError> {

    @Autowired
    public AllTaskErrors(CouchDbConnector db) {
        super(TaskError.class, db);
    }

    public List<TaskError> byTaskId(final String taskId) {
        return queryView("by_taskId", taskId);
    }

}

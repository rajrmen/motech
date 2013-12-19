package org.motechproject.tasks.repository;

import org.ektorp.support.View;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.tasks.domain.TaskActivity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@View(name = "by_taskId", map = "function(doc) { if(doc.type === 'TaskActivity') emit(doc.task); }")
public class AllTaskActivities extends MotechBaseRepository<TaskActivity> {


    public AllTaskActivities() {
        super("motech-tasks",TaskActivity.class);
    }

    public List<TaskActivity> byTaskId(final String taskId) {
        return queryView("by_taskId", taskId);
    }

}

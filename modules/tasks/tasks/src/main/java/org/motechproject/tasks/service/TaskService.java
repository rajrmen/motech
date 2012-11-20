package org.motechproject.tasks.service;

import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskEvent;
import org.motechproject.tasks.ex.ActionNotFoundException;
import org.motechproject.tasks.ex.TriggerNotFoundException;

import java.util.List;

public interface TaskService {
    Integer CHANNEL_NAME_IDX = 0;
    Integer MODULE_NAME_IDX = 1;
    Integer MODULE_VERSION_IDX = 2;
    Integer SUBJECT_IDX = 3;

    void save(final Task task);

    TaskEvent getActionEventFor(Task task) throws ActionNotFoundException;

    List<Task> getAllTasks();

    List<Task> findTasksForTrigger(final TaskEvent trigger);

    TaskEvent findTrigger(String subject) throws TriggerNotFoundException;
}

package org.motechproject.tasks.service;

import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskEvent;

import java.util.List;
import java.util.Map;

public interface TaskService {

    void save(final Task task);

    TaskEvent getActionEventFor(Task task);

    List<Task> getAllTasks();

    List<Task> findTasksForTrigger(String subject);

}

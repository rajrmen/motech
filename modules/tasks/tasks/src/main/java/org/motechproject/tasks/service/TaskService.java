package org.motechproject.tasks.service;

import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskEvent;

public interface TaskService {

    void add(final Task task);

    TaskEvent getTriggerEventFor(Task task);

    TaskEvent getActionEventFor(Task task);
}

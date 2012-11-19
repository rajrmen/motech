package org.motechproject.tasks.service;

import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskError;

import java.util.List;

public interface TaskErrorService {
    void addTaskError(Task task, String errorMessage);

    List<TaskError> getTaskErrors(Task task);
}

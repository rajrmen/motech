package org.motechproject.tasks.service.impl;

import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskError;
import org.motechproject.tasks.repository.AllTaskErrors;
import org.motechproject.tasks.service.TaskErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskErrorServiceImpl implements TaskErrorService {
    private AllTaskErrors allTaskErrors;

    @Autowired
    public TaskErrorServiceImpl(AllTaskErrors allTaskErrors) {
        this.allTaskErrors = allTaskErrors;
    }

    @Override
    public void addTaskError(final Task task, final String errorMessage) {
        allTaskErrors.add(new TaskError(task.getId(), errorMessage));
    }

    @Override
    public List<TaskError> getTaskErrors(final Task task) {
        return allTaskErrors.byTaskId(task.getId());
    }
}

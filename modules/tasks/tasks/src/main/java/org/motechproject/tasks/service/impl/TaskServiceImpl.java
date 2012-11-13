package org.motechproject.tasks.service.impl;

import org.motechproject.dao.BusinessIdNotUniqueException;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.repository.AllTasks;
import org.motechproject.tasks.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class TaskServiceImpl implements TaskService {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private AllTasks allTasks;

    @Autowired
    public TaskServiceImpl(final AllTasks allTasks) {
        this.allTasks = allTasks;
    }

    @Override
    public void add(Task task) {
        try {
            allTasks.addOrUpdate(task);
            logger.info(String.format("Saved task: %s", task.getId()));
        } catch (BusinessIdNotUniqueException e) {
            logger.error(e.getMessage(), e);
        }
    }
}

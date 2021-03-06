package org.motechproject.tasks.service;

import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskActivity;
import org.motechproject.tasks.ex.TaskHandlerException;

import java.util.List;

/**
 * Service for managing task activities. Task activities are used for storing information about past task executions.
 */
public interface TaskActivityService {

    /**
     * Logs an execution error for the given task.
     *
     * @param task  the failed task, not null
     * @param e  the cause of the error, not null
     */
    void addError(Task task, TaskHandlerException e);

    /**
     * Logs an execution success for the given task.
     *
     * @param task  the succeeded task, not null
     */
    void addSuccess(Task task);

    /**
     * Logs a warning for the given task.
     *
     * @param task  the task, not null
     */
    void addWarning(Task task);

    /**
     * Logs a warning for the given task.
     *
     * @param task  the task, not null
     * @param key  the key of the message
     * @param value  the name of the field that caused the warning
     */
    void addWarning(Task task, String key, String value);

    /**
     * Logs a warning for the given task.
     *
     * @param task  the task, not null
     * @param key  the key of the message
     * @param field  the name of the failed that caused the warning, not null
     * @param e  the exception that caused the warning, not null
     */
    void addWarning(Task task, String key, String field, Exception e);

    /**
     * Deletes all activities for the task with the given ID.
     *
     * @param taskId  the task ID, not null
     */
    void deleteActivitiesForTask(Long taskId);

    /**
     * Returns all activities as a list ordered by date.
     *
     * @return the list of all activities
     */
    List<TaskActivity> getAllActivities();

    /**
     * Returns list of all activities for task with the given ID.
     *
     * @param taskId  the task ID, null returns null
     * @return  the list of all activities for task with given ID
     */
    List<TaskActivity> getTaskActivities(Long taskId);
}

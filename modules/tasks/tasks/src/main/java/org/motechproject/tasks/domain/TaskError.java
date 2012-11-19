package org.motechproject.tasks.domain;

import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type == 'TaskError'")
public class TaskError extends MotechBaseDataObject {
    private String message;
    private String task;
    private DateTime date = DateTime.now();

    public TaskError() {
        this(null, null);
    }

    public TaskError(String task, String message) {
        this.task = task;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public String getTask() {
        return task;
    }

    public void setTask(final String task) {
        this.task = task;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(final DateTime date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TaskError taskError = (TaskError) o;

        if (date != null ? !date.equals(taskError.date) : taskError.date != null) {
            return false;
        }

        if (message != null ? !message.equals(taskError.message) : taskError.message != null) {
            return false;
        }

        if (task != null ? !task.equals(taskError.task) : taskError.task != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = message != null ? message.hashCode() : 0;
        result = 31 * result + (task != null ? task.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);

        return result;
    }

    @Override
    public String toString() {
        return String.format("TaskError{message='%s', task='%s', date=%s}", message, task, date);
    }
}

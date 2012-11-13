package org.motechproject.tasks.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

import java.util.HashMap;
import java.util.Map;

@TypeDiscriminator("doc.type == 'Task'")
public class Task extends MotechBaseDataObject {
    protected final String trigger;
    protected final String action;
    protected final Map<String, String> actionInputFields;

    protected Map<String, String> additionalData = new HashMap<>();
    protected Map<String, String> filter = new HashMap<>();

    public Task(final String trigger, final String action, final Map<String, String> actionInputFields) {
        this.trigger = trigger;
        this.action = action;
        this.actionInputFields = actionInputFields;
    }

    public String getTrigger() {
        return trigger;
    }

    public String getAction() {
        return action;
    }

    public Map<String, String> getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(final Map<String, String> additionalData) {
        this.additionalData.clear();
        this.additionalData.putAll(additionalData);
    }

    public Map<String, String> getActionInputFields() {
        return actionInputFields;
    }

    public void setFilter(final Map<String, String> filter) {
        this.filter.clear();
        this.filter.putAll(filter);
    }

    public Map<String, String> getFilter() {
        return filter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Task task = (Task) o;

        if (!action.equals(task.action)) {
            return false;
        }
        if (!actionInputFields.equals(task.actionInputFields)) {
            return false;
        }
        if (additionalData != null ? !additionalData.equals(task.additionalData) : task.additionalData != null) {
            return false;
        }
        if (filter != null ? !filter.equals(task.filter) : task.filter != null) {
            return false;
        }
        if (!trigger.equals(task.trigger)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = trigger.hashCode();
        result = 31 * result + action.hashCode();
        result = 31 * result + actionInputFields.hashCode();
        result = 31 * result + (additionalData != null ? additionalData.hashCode() : 0);
        result = 31 * result + (filter != null ? filter.hashCode() : 0);

        return result;
    }

    @Override
    public String toString() {
        return String.format("Task{trigger='%s', action='%s', actionInputFields=%s, additionalData=%s, filter=%s}",
                trigger, action, actionInputFields, additionalData, filter);
    }
}

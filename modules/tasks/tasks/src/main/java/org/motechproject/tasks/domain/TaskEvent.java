package org.motechproject.tasks.domain;

import java.util.List;

public class TaskEvent {
    protected List<EventParameter> eventParameters;
    protected String description;
    protected String eventKey;
    protected String displayName;

    public List<EventParameter> getEventParameters() {
        return eventParameters;
    }

    public void setEventParameters(List<EventParameter> eventParameters) {
        this.eventParameters = eventParameters;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        if (eventKey == null || eventKey.split(".").length < 2) {
            throw new IllegalStateException("EventKey property must have pattern: className.propertyName");
        }

        this.eventKey = eventKey;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TaskEvent taskEvent = (TaskEvent) o;

        if (description != null ? !description.equals(taskEvent.description) : taskEvent.description != null) {
            return false;
        }

        if (displayName != null ? !displayName.equals(taskEvent.displayName) : taskEvent.displayName != null) {
            return false;
        }

        if (eventKey != null ? !eventKey.equals(taskEvent.eventKey) : taskEvent.eventKey != null) {
            return false;
        }

        if (eventParameters != null ? !eventParameters.equals(taskEvent.eventParameters) : taskEvent.eventParameters != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = eventParameters != null ? eventParameters.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (eventKey != null ? eventKey.hashCode() : 0);
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);

        return result;
    }

    @Override
    public String toString() {
        return String.format("TaskEvent{eventParameters=%s, description='%s', eventKey='%s', displayName='%s'}",
                eventParameters, description, eventKey, displayName);
    }
}

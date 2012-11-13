package org.motechproject.tasks.domain;

import java.util.List;

public class Event {
    protected final String subject;
    protected String description;
    protected final List<EventParameter> eventParameters;

    public Event(final String subject, final List<EventParameter> eventParameters) {
        this.subject = subject;
        this.eventParameters = eventParameters;
    }

    public String getSubject() {
        return subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public List<EventParameter> getEventParameters() {
        return eventParameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Event event = (Event) o;

        return !(description != null ? !description.equals(event.description) : event.description != null) &&
                eventParameters.equals(event.eventParameters) && subject.equals(event.subject);

    }

    @Override
    public int hashCode() {
        int result = subject.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + eventParameters.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("Event{subject='%s', description='%s', eventParameters=%s}", subject, description, eventParameters);
    }

}

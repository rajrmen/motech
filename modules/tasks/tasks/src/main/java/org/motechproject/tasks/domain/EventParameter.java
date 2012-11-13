package org.motechproject.tasks.domain;

public class EventParameter {
    protected final String eventKey;
    protected final String displayName;

    public EventParameter(String eventKey, String displayName) {
        this.eventKey = eventKey;
        this.displayName = displayName;
    }

    public String getEventKey() {
        return eventKey;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EventParameter that = (EventParameter) o;

        return displayName.equals(that.displayName) && eventKey.equals(that.eventKey);
    }

    @Override
    public int hashCode() {
        int result = eventKey.hashCode();
        result = 31 * result + displayName.hashCode();

        return result;
    }

    @Override
    public String toString() {
        return String.format("EventParameter{eventKey='%s', displayName='%s'}", eventKey, displayName);
    }

}

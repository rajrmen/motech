package org.motechproject.tasks.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

import java.util.ArrayList;
import java.util.List;

@TypeDiscriminator("doc.type == 'Channel'")
public class Channel extends MotechBaseDataObject {
    protected final String displayName;
    protected final String moduleName;
    protected final String moduleVersion;
    protected List<Event> triggerEvents = new ArrayList<>();
    protected List<Event> actionEvents = new ArrayList<>();

    public Channel(final String displayName, final String moduleName, final String moduleVersion) {
        this.displayName = displayName;
        this.moduleName = moduleName;
        this.moduleVersion = moduleVersion;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getModuleVersion() {
        return moduleVersion;
    }

    public List<Event> getTriggerEvents() {
        return triggerEvents;
    }

    public void setTriggerEvents(final List<Event> triggerEvents) {
        this.triggerEvents.clear();
        this.triggerEvents.addAll(triggerEvents);
    }

    public List<Event> getActionEvents() {
        return actionEvents;
    }

    public void setActionEvents(final List<Event> actionEvents) {
        this.actionEvents.clear();
        this.actionEvents.addAll(actionEvents);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Channel channel = (Channel) o;

        return actionEvents.equals(channel.actionEvents) && displayName.equals(channel.displayName) &&
                moduleName.equals(channel.moduleName) && moduleVersion.equals(channel.moduleVersion) &&
                triggerEvents.equals(channel.triggerEvents);

    }

    @Override
    public int hashCode() {
        int result = displayName.hashCode();
        result = 31 * result + moduleName.hashCode();
        result = 31 * result + moduleVersion.hashCode();
        result = 31 * result + triggerEvents.hashCode();
        result = 31 * result + actionEvents.hashCode();

        return result;
    }

    @Override
    public String toString() {
        return String.format("Channel{displayName='%s', moduleName='%s', moduleVersion='%s', triggerEvents=%s, actionEvents=%s}",
                displayName, moduleName, moduleVersion, triggerEvents, actionEvents);
    }

}

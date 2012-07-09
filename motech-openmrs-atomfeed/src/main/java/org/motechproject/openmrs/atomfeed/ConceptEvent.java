package org.motechproject.openmrs.atomfeed;

import org.motechproject.openmrs.atomfeed.events.EventSubjects;
import org.motechproject.openmrs.atomfeed.model.Entry;

public class ConceptEvent extends EventBuilderTemplate {

    public ConceptEvent(Entry entry) {
        super(entry);
    }

    @Override
    protected String getCreateAction() {
        return EventSubjects.CONCEPT_CREATE;
    }

    @Override
    protected String getUpdateAction() {
        return EventSubjects.CONCEPT_UPDATED;
    }

    @Override
    protected String getVoidedAction() {
        throw new RuntimeException("Concepts cannot have voided action");
    }

    @Override
    protected String getDeleteAction() {
        return EventSubjects.CONCEPT_DELETED;
    }

}

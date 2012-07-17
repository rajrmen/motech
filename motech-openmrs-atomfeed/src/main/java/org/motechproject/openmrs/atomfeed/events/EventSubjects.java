package org.motechproject.openmrs.atomfeed.events;

public class EventSubjects {

    private static final String BASE_SUBJECT = "org.motechproject.openmrs.atomfeed";
    
    public static final String POLLING_SUBJECT = BASE_SUBJECT + ".poll";
    
    // Patients
    public static final String PATIENT_CREATE = BASE_SUBJECT + ".create.patient";
    public static final String PATIENT_UPDATE = BASE_SUBJECT + ".update.patient";
    public static final String PATIENT_VOIDED = BASE_SUBJECT + ".voided.patient";
    public static final String PATIENT_DELETED = BASE_SUBJECT + ".deleted.patient";

    // Concepts
    public static final String CONCEPT_CREATE = BASE_SUBJECT + ".create.concept";
    public static final String CONCEPT_UPDATED = BASE_SUBJECT + ".update.concept";
    public static final String CONCEPT_DELETED = BASE_SUBJECT + ".deleted.concept";
}

package org.motechproject.mrs.util;

import org.motechproject.mrs.services.PatientAdapter;

public class DefaultPatientAdapter {
    String name;
    PatientAdapter patientAdapter;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PatientAdapter getPatientAdapter() {
        return patientAdapter;
    }

    public void setPatientAdapter(PatientAdapter patientAdapter) {
        this.patientAdapter = patientAdapter;
    }
}

package org.motechproject.openmrs.model;

import org.motechproject.mrs.domain.Person;
import org.motechproject.mrs.domain.Provider;

public class OpenMRSProvider implements Provider {

    private String providerId;
    private Person person;

    public OpenMRSProvider(Person person) {
        this.person = person;
    }

    public OpenMRSProvider() {
    }

    @Override
    public Person getPerson() {
        return person;
    }

    @Override
    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public String getProviderId() {
        return providerId;
    }

    @Override
    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

}

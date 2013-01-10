package org.motechproject.mrs.services;

import org.motechproject.mrs.model.MRSPerson;

/**
 * Interface for handling Persons
 */
public interface MRSPersonAdapter {

    /**
     * Fetches a person by the given person id
     *
     * @param personId Value to be used to find a person
     * @return Parent with the given person id if exists
     */
    MRSPerson getPerson(String personId);

}

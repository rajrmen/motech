package org.motechproject.mds.service;

import org.motechproject.mds.domain.Entity;

import javax.jdo.metadata.JDOMetadata;

/**
 * This interface provide method to create a class for the given entity. The implementation of this
 * interface should also construct other classes like repository, service interface and
 * implementation for this service interface.
 */
public interface MDSConstructor {

    JDOMetadata getCurrentMetadata();

    void constructEntity(Entity entity);

    void generateAllEntities();
}

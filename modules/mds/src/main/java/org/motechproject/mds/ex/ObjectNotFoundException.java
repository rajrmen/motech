package org.motechproject.mds.ex;

public class ObjectNotFoundException extends MdsException {

    public ObjectNotFoundException() {
        super("mds.error.objectNotFound");
    }
}

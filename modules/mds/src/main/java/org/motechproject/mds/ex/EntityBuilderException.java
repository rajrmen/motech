package org.motechproject.mds.ex;

public class EntityBuilderException extends MdsException {
    private static final long serialVersionUID = -3828736679522726438L;

    /**
     * Constructs a new EntityBuilderException with <i>mds.error.entityBuilderFailure</i> as
     * a message key.
     */
    public EntityBuilderException(Throwable cause) {
        super("mds.error.entityBuilderFailure", cause);
    }
}

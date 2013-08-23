package org.motechproject.security.ex;

public class RoleHasUserException extends RuntimeException {

    public RoleHasUserException() {
        super();
    }

    public RoleHasUserException(String message) {
        super(message);
    }
}

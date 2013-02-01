package org.motechproject.tenant.ex;

public class NoTenantException extends RuntimeException {

    public NoTenantException() {
        super();
    }

    public NoTenantException(String message) {
        super(message);
    }

    public NoTenantException(String message, Throwable cause) {
        super(message, cause);
    }
}

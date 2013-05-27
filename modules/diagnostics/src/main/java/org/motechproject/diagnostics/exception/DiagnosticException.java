package org.motechproject.diagnostics.exception;

public class DiagnosticException extends RuntimeException {

    public DiagnosticException(Exception ex){
        super(ex);
    }

}

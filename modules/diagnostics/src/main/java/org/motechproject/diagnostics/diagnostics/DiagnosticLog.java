package org.motechproject.diagnostics.diagnostics;

import org.hibernate.exception.ExceptionUtils;

public class DiagnosticLog {

    private StringBuilder log = new StringBuilder();

    public DiagnosticLog() {
    }

    public void add(String message) {
        log.append(message + "\n");
    }

    public void addError(Exception e) {
        log.append("EXCEPTION: " + ExceptionUtils.getFullStackTrace(e)+"\n\n");
    }

    @Override
    public String toString() {
        return log.toString();
    }
}
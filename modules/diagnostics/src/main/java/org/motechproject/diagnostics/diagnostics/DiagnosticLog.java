package org.motechproject.diagnostics.diagnostics;


public class DiagnosticLog {

    private StringBuilder log = new StringBuilder();

    public DiagnosticLog() {
    }

    public void add(String message) {
        log.append(message + "\n");
    }

    public void addError(Exception e) {
        log.append("EXCEPTION: " + e.getStackTrace() + "\n\n");
    }

    @Override
    public String toString() {
        return log.toString();
    }
}
package org.motechproject.diagnostics.response;

public class DiagnosticsResult {
    public static DiagnosticsResult NULL = new DiagnosticsResult(DiagnosticsStatus.UNKNOWN, "Null Result");
    private DiagnosticsStatus status;
    private String message;

    public DiagnosticsResult(DiagnosticsStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public DiagnosticsStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}

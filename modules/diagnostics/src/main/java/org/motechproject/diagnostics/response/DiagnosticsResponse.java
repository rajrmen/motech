package org.motechproject.diagnostics.response;

public class DiagnosticsResponse {
    private String name;
    private DiagnosticsResult result;

    public DiagnosticsResponse(String name, DiagnosticsResult result) {
        this.name = name;
        this.result = result;
    }

    public String getName() {
        return name;
    }

    public DiagnosticsResult getResult() {
        return result;
    }
}

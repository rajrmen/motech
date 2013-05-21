package org.motechproject.diagnostics.response;

public class ExceptionResponse {
    private String message;
    private String trace;

    public ExceptionResponse(String message, String trace) {
        this.message = message;
        this.trace = trace;
    }

    public String getMessage() {
        return message;
    }

    public String getTrace() {
        return trace;
    }

    public String toString() {
        return "{" +
                    "\"message\" : \"" + message + "\"," +
                    "\"trace\" : \"" + trace + "\"" +
                "}";
    }
}

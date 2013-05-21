package org.motechproject.diagnostics.controller;

import org.motechproject.diagnostics.DiagnosticResponseBuilder;
import org.motechproject.diagnostics.repository.AllDiagnosticMethods;
import org.motechproject.diagnostics.response.DiagnosticsResponse;
import org.motechproject.diagnostics.response.ExceptionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Controller
@RequestMapping(value = "/diagnostics")
public class DiagnosticsController {

    AllDiagnosticMethods allDiagnosticMethods;
    private DiagnosticResponseBuilder diagnosticResponseBuilder;

    @Autowired
    public DiagnosticsController(AllDiagnosticMethods allDiagnosticMethods,DiagnosticResponseBuilder diagnosticResponseBuilder) {
        this.allDiagnosticMethods = allDiagnosticMethods;
        this.diagnosticResponseBuilder = diagnosticResponseBuilder;
    }

    @RequestMapping(method = RequestMethod.GET)
    public void getDiagnostics(HttpServletResponse response) throws InvocationTargetException, IllegalAccessException, IOException {
        List<DiagnosticsResponse> diagnosticsResponses = allDiagnosticMethods.runAllDiagnosticMethods();
        String diagnosticsResponse = diagnosticResponseBuilder.createResponseMessage(diagnosticsResponses);
        response.getOutputStream().print(diagnosticsResponse);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public String handleException(final Exception exception, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        StringWriter stringWriter = new StringWriter();
        exception.printStackTrace(new PrintWriter(stringWriter));
        return new ExceptionResponse(exception.getMessage(), stringWriter.toString()).toString();
    }
}

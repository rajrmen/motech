package org.motechproject.diagnostics.model;

import org.hibernate.exception.ExceptionUtils;
import org.motechproject.diagnostics.annotation.Diagnostic;
import org.motechproject.diagnostics.diagnostics.DiagnosticLog;
import org.motechproject.diagnostics.response.DiagnosticsResponse;
import org.motechproject.diagnostics.response.DiagnosticsResult;
import org.motechproject.diagnostics.response.DiagnosticsStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DiagnosticMethod {
    private String name;
    private Method method;
    private Object bean;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public DiagnosticMethod(String name, Object bean, Method method) {
        this.name = name;
        this.method = method;
        this.bean = bean;
    }

    public static boolean isValidDiagnosticMethod(Method method) {
        return method.isAnnotationPresent(Diagnostic.class);
    }

    public DiagnosticsResponse run() throws InvocationTargetException, IllegalAccessException {
        DiagnosticsResult result;
        try {
            result = (DiagnosticsResult) method.invoke(bean, null);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getFullStackTrace(e));
            result = exceptionResult(e);
        }

        if (result == null) {
            result = DiagnosticsResult.NULL;
        }

        return new DiagnosticsResponse(name, result);
    }

    private DiagnosticsResult exceptionResult(Exception e) {
        DiagnosticLog diagnosticLog = new DiagnosticLog();
        diagnosticLog.add("Exception occurred.");
        diagnosticLog.addError(e);
        return new DiagnosticsResult(DiagnosticsStatus.FAIL, diagnosticLog.toString());
    }
}

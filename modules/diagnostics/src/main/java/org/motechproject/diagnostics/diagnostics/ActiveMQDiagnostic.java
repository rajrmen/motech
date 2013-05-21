package org.motechproject.diagnostics.diagnostics;

import org.motechproject.diagnostics.annotation.Diagnostic;
import org.motechproject.diagnostics.response.DiagnosticsResult;
import org.motechproject.diagnostics.response.DiagnosticsStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.connection.CachingConnectionFactory;

import javax.jms.Connection;
import javax.jms.JMSException;

public class ActiveMQDiagnostic {

    private CachingConnectionFactory cachingConnectionFactory;

    @Autowired
    public ActiveMQDiagnostic(@Qualifier("activeMQDiagnosticsConnectionFactory") CachingConnectionFactory cachingConnectionFactory) {
        this.cachingConnectionFactory = cachingConnectionFactory;
    }

    @Diagnostic(name = "ACTIVEMQ")
    public DiagnosticsResult performDiagnosis() throws JMSException {
        DiagnosticLog diagnosticLog = new DiagnosticLog();

        DiagnosticsStatus status = checkActiveMQConnection(diagnosticLog);
        return new DiagnosticsResult(status, diagnosticLog.toString());
    }

    private DiagnosticsStatus checkActiveMQConnection(DiagnosticLog diagnosticLog) {
        diagnosticLog.add("Checking for ActiveMQ connection ...");
        Connection connection = null;
        DiagnosticsStatus status = DiagnosticsStatus.PASS;
        try {
            connection = cachingConnectionFactory.getTargetConnectionFactory().createConnection();
            connection.start();
            diagnosticLog.add("Successfully opened a connection.");
        } catch (Exception ex) {
            diagnosticLog.add("Could not open a connection.");
            diagnosticLog.addError(ex);
            status = DiagnosticsStatus.FAIL;
        } finally {
            if(connection != null) {
                try {
                    connection.close();
                    diagnosticLog.add("Successfully closed the connection.");
                } catch (JMSException e) {
                    diagnosticLog.add("Could not close the connection.");
                    diagnosticLog.addError(e);
                    status = DiagnosticsStatus.FAIL;
                }
            }
        }
        return status;
    }
}

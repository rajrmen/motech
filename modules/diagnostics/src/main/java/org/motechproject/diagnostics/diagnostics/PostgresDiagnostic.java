package org.motechproject.diagnostics.diagnostics;

import org.motechproject.diagnostics.annotation.Diagnostic;
import org.motechproject.diagnostics.response.DiagnosticsResult;
import org.motechproject.diagnostics.response.DiagnosticsStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class PostgresDiagnostic {

    private Properties postgresProperties;

    @Autowired
    public PostgresDiagnostic(@Qualifier("postgresProperties") Properties postgresProperties) {
        this.postgresProperties = postgresProperties;
    }

//    @Diagnostic(name = "POSTGRES DATABASE CONNECTION")
    public DiagnosticsResult performDiagnosis() {
        DiagnosticLog diagnosticLog = new DiagnosticLog();
        diagnosticLog.add("Opening session with database ...");

        String url = postgresProperties.getProperty("jdbc.url");
        String userName = postgresProperties.getProperty("jdbc.username");
        String password = postgresProperties.getProperty("jdbc.password");
        Connection connection = null;
        DiagnosticsStatus status = DiagnosticsStatus.PASS;
        try {
            connection = getConnection(url, userName, password);
            diagnosticLog.add("Successfully opened a session.");
        } catch (SQLException e) {
            diagnosticLog.add("Could not open a session.");
            diagnosticLog.addError(e);
            status = DiagnosticsStatus.FAIL;
        } finally {
            if(connection != null) {
                try {
                    connection.close();
                    diagnosticLog.add("Successfully closed the session.");
                } catch (SQLException e) {
                    diagnosticLog.add("Could not close the session.");
                    diagnosticLog.addError(e);
                    status = DiagnosticsStatus.FAIL;
                }
            }
        }
        return new DiagnosticsResult(status, diagnosticLog.toString());
    }

    protected Connection getConnection(String url, String userName, String password) throws SQLException {
        return DriverManager.getConnection(url, userName, password);
    }

}
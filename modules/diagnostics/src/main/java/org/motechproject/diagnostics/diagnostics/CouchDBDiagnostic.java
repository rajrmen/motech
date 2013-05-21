package org.motechproject.diagnostics.diagnostics;

import org.ektorp.impl.StdCouchDbInstance;
import org.motechproject.diagnostics.annotation.Diagnostic;
import org.motechproject.diagnostics.response.DiagnosticsResult;
import org.motechproject.diagnostics.response.DiagnosticsStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Properties;

public class CouchDBDiagnostic {

    private Properties couchdbProperties;
    private StdCouchDbInstance dbInstance;
    private List<String> databases;

    @Autowired
    public CouchDBDiagnostic(@Qualifier("couchdbProperties") Properties couchdbProperties, @Qualifier("diagnosticsDbInstance") StdCouchDbInstance dbInstance,
                             List<String> databases) {
        this.dbInstance = dbInstance;
        this.databases = databases;
        this.couchdbProperties = couchdbProperties;
    }

    @Diagnostic(name = "COUCH DATABASES")
    public DiagnosticsResult performDiagnosis() {
        DiagnosticLog diagnosticLog = new DiagnosticLog();
        boolean isSuccessful = performDiagnosisForConnection(diagnosticLog) & performDiagnosisForDatabases(diagnosticLog);
        return new DiagnosticsResult(isSuccessful ? DiagnosticsStatus.PASS : DiagnosticsStatus.FAIL, diagnosticLog.toString());
    }

    private boolean performDiagnosisForConnection(DiagnosticLog diagnosticLog) {
        diagnosticLog.add("Checking couch db connection ...");
        try {
            dbInstance.getConnection().head("/");
            diagnosticLog.add("Successfully connected to couch database.");
            diagnosticLog.add("Databases present : " + dbInstance.getAllDatabases().toString());
            return true;
        } catch (Exception e) {
            diagnosticLog.add("Couch DB connection failed.");
            diagnosticLog.addError(e);
            return false;
        }
    }

    private boolean performDiagnosisForDatabases(DiagnosticLog diagnosticLog) {
        diagnosticLog.add("Checking couch dbs ...");
        RestTemplate restTemplate = new RestTemplate();
        boolean allSuccessful = true;
        for(String database: databases) {
            allSuccessful = checkDatabase(database, diagnosticLog, restTemplate) && allSuccessful;
        }

        return allSuccessful;
    }

    private boolean checkDatabase(String database, DiagnosticLog diagnosticLog, RestTemplate restTemplate) {
        int statusCode = 200;
        try {
            restTemplate.getForEntity(getUrlFor(database), String.class);
        } catch(HttpClientErrorException e) {
            statusCode = e.getStatusCode().value();
        }
        diagnosticLog.add(database + " : HTTP Status Code: " + statusCode);
        return statusCode == 200;
    }

    private String getUrlFor(String database) {
        String url = "http://%s:%s/%s";
        return String.format(url,
                couchdbProperties.getProperty("host"),
                couchdbProperties.getProperty("port"),
                database);
    }
}
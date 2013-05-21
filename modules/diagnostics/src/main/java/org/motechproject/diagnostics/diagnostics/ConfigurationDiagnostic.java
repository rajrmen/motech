package org.motechproject.diagnostics.diagnostics;

import org.motechproject.diagnostics.annotation.Diagnostic;
import org.motechproject.diagnostics.response.DiagnosticsResult;
import org.motechproject.diagnostics.response.DiagnosticsStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;

public class ConfigurationDiagnostic {

    private Map<String, Properties> propertyFilesMap;

    @Autowired
    public ConfigurationDiagnostic(Map<String, Properties> propertyFilesMap) {
        this.propertyFilesMap = propertyFilesMap;
    }

    @Diagnostic(name = "CONFIGURATION PROPERTIES")
    public DiagnosticsResult performDiagnosis() {
        if (propertyFilesMap == null) return null;
        DiagnosticLog diagnosticLog = new DiagnosticLog();

        for (Map.Entry<String, Properties> propertiesMap : propertyFilesMap.entrySet())
            logPropertiesFileFor(diagnosticLog, propertiesMap.getKey(), propertiesMap.getValue());

        return new DiagnosticsResult(DiagnosticsStatus.PASS, diagnosticLog.toString());
    }

    private void logPropertiesFileFor(DiagnosticLog diagnosticLog, String file, Properties properties) {
        diagnosticLog.add(file + ":\n");
        TreeSet sortedKeys = new TreeSet(properties.keySet());
        for (Object key : sortedKeys)
            diagnosticLog.add(key + "=" + properties.get(key));
        diagnosticLog.add("______________________________________________________________");
    }
}

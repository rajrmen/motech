package org.motechproject.commons.api;

import java.util.List;

/**
 * The <code>CsvConverter</code> class, provides methods responsible for conversion to CSV-formatted strings.
 */

public class CsvConverter {
    private String csvString = "";
    private static final String SEPARATOR = ",";
    private static final String END_OF_LINE = "\r\n";

    public CsvConverter() {
    }

    public String convertToCSV(List<List<Object>> list) {
        for (List<Object> line : list) {
            for (Object o : line) {
                String csvObject = o.toString();
                if (csvObject.contains(SEPARATOR) || csvObject.contains("\"") || csvObject.contains(END_OF_LINE)) {
                    csvObject = csvObject.replace("\"", "\"\"");
                    csvObject = "\""+csvObject+"\"";
                }
                csvString = csvString.concat(csvObject+SEPARATOR);
            }

            //removing last, unnecessary separator
            csvString = csvString.substring(0, csvString.lastIndexOf(SEPARATOR));
            csvString = csvString.concat(END_OF_LINE);
        }

        return csvString;
    }
}

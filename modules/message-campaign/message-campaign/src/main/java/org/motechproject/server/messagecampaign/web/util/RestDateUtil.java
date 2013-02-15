package org.motechproject.server.messagecampaign.web.util;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

public final class RestDateUtil {

    private RestDateUtil() {
        // static utility class
    }

    private static DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendDayOfWeekShortText()
            .appendLiteral(", ").appendDayOfMonth(1).appendLiteral(' ').appendMonthOfYearShortText().appendLiteral(' ')
            .appendYear(4, 4).toFormatter();

    public static LocalDate parseString(String dateStr) {
        return formatter.parseLocalDate(dateStr);
    }

    public static String print(LocalDate localDate) {
        return formatter.print(localDate);
    }
}

package org.motechproject.server.messagecampaign.web.util;

import org.joda.time.LocalDate;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RestDateUtilTest {

    @Test
    public void testDateParsing() {
        LocalDate expected = new LocalDate(2013, 8, 18);
        LocalDate actual = RestDateUtil.parseString("Sun, 18 Aug 2013");

        assertEquals(expected, actual);
    }

    @Test
    public void testDateFormatting() {
        LocalDate date = new LocalDate(2013, 8, 18);
        String formattedDate = RestDateUtil.print(date);

        assertEquals("Sun, 18 Aug 2013", formattedDate);
    }
}

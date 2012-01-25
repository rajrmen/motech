package org.motechproject.adherence.service;

import org.joda.time.DateTime;
import org.motechproject.util.DateTimeSourceUtil;

public class BaseUnitTest {

    private MockedDateTimeSource dateTimeSource = new MockedDateTimeSource();

    public BaseUnitTest() {
        DateTimeSourceUtil.SourceInstance = dateTimeSource;
    }

    protected void mockTime(DateTime now) {
        dateTimeSource.setNow(now);
    }

    protected void resetTime() {
        dateTimeSource.reset();
    }
}

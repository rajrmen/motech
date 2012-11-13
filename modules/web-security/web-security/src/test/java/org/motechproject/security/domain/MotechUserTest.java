package org.motechproject.security.domain;


import org.junit.Ignore;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class MotechUserTest {

    @Ignore
    @Test
    public void userNameShouldBeSetToLowercase() {

        MotechUserCouchdbImpl user = new MotechUserCouchdbImpl("TestUser", "p@ssw0rd", "", "", null);
        assertEquals("testuser", user.getUserName());
    }

    @Ignore
    @Test
    public void shouldHandleNullValueForUserName() {
        MotechUserCouchdbImpl user = new MotechUserCouchdbImpl(null, "p@ssw0rd", "","", null);
        assertEquals(null, user.getUserName());
    }

}

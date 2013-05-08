package org.motechproject.couch.mrs.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.couch.mrs.model.CouchMrsUser;
import org.motechproject.couch.mrs.model.CouchPerson;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/META-INF/motech/*.xml")
public class CouchMrsUserAdapterIT {

    @Autowired
    private CouchMrsUserAdapter couchMrsUserAdapter;

    @Test
    public void shouldSaveAndRetrieveUser() throws UserAlreadyExistsException {
        String userName = unique("foo");

        assertNull(couchMrsUserAdapter.getUserByUserName(userName));

        couchMrsUserAdapter.saveUser(new CouchMrsUser("id", "sysid", "admin", userName, new CouchPerson()));

        assertNotNull(couchMrsUserAdapter.getUserByUserName(userName));
    }

    @Test(expected = UserAlreadyExistsException.class)
    public void shouldThrowExceptionIfUserAlreadyExists() throws UserAlreadyExistsException {
        String userName = unique("foo");

        couchMrsUserAdapter.saveUser(new CouchMrsUser("id", "sysid", "admin", userName, new CouchPerson()));

        couchMrsUserAdapter.saveUser(new CouchMrsUser("id2", "sysid2", "user", userName, new CouchPerson()));
    }

    @Test
    public void shouldRetrieveAllUsers() throws UserAlreadyExistsException {
        String userName1 = unique("foo1");
        String userName2 = unique("foo2");
        String userName3 = unique("foo3");

        int initialSize = couchMrsUserAdapter.getAllUsers().size();

        couchMrsUserAdapter.saveUser(new CouchMrsUser("id", "sysid", "admin", userName1, new CouchPerson()));
        couchMrsUserAdapter.saveUser(new CouchMrsUser("id", "sysid", "admin", userName2, new CouchPerson()));
        couchMrsUserAdapter.saveUser(new CouchMrsUser("id", "sysid", "admin", userName3, new CouchPerson()));

        assertEquals(3, couchMrsUserAdapter.getAllUsers().size() - initialSize);
    }

    private String unique(String key) {
        return UUID.randomUUID().toString() + "-" + key;
    }
}

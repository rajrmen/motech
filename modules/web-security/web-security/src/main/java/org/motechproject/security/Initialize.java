package org.motechproject.security;

import org.ektorp.CouchDbConnector;
import org.motechproject.security.domain.*;
import org.motechproject.security.repository.AllMotechPermissions;
import org.motechproject.security.repository.AllMotechRoles;
import org.motechproject.security.repository.AllMotechUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Arrays;


public class Initialize {

    @Autowired
    AllMotechPermissions allMotechPermissions;

    @Autowired
    AllMotechRoles allMotechRoles;

    @Autowired
    AllMotechUsers allMotechUsers;

    @Autowired
    public void initialize(@Qualifier("webSecurityDbConnector") CouchDbConnector db) {
        MotechPermission addUserPerrmision = new MotechPermissionCouchdbImpl("addUser");
        MotechPermission editUserPermission = new MotechPermissionCouchdbImpl("editUser");
        MotechPermission deleteUserPermission = new MotechPermissionCouchdbImpl("deleteUser");
        MotechRole adminUser = new MotechRoleCouchdbImpl("ADMIN USERS", Arrays.asList(addUserPerrmision, editUserPermission, deleteUserPermission));
        MotechUser admin = new MotechUserCouchdbImpl("motech", "motech", "1", "", Arrays.asList(adminUser));
        allMotechPermissions.add(addUserPerrmision);
        allMotechPermissions.add(editUserPermission);
        allMotechPermissions.add(deleteUserPermission);
        allMotechRoles.add(adminUser);
        allMotechUsers.add(admin);
    }
}

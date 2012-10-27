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
        //initialize startup permission for admin user
        MotechPermission addUserPerrmision = new MotechPermissionCouchdbImpl("addUser");
        MotechPermission editUserPermission = new MotechPermissionCouchdbImpl("editUser");
        MotechPermission deleteUserPermission = new MotechPermissionCouchdbImpl("deleteUser");

        //initialize startup permission for admin bundle
        MotechPermission startBundle = new MotechPermissionCouchdbImpl("startBundle");
        MotechPermission stopBundle = new MotechPermissionCouchdbImpl("storpBundle");
        MotechPermission restartBundle = new MotechPermissionCouchdbImpl("restartBundle");
        MotechPermission uninstallBundle = new MotechPermissionCouchdbImpl("uninstallBundle");
        MotechPermission installBundle = new MotechPermissionCouchdbImpl("installBundle");
        MotechPermission changeConfigBundle = new MotechPermissionCouchdbImpl("chancgCongigBundle");

        //initialize startup role
        MotechRole adminUser = new MotechRoleCouchdbImpl("Admin User", Arrays.asList(addUserPerrmision.getPermissionName(), editUserPermission.getPermissionName(), deleteUserPermission.getPermissionName()));
        MotechRole adminBundle = new MotechRoleCouchdbImpl("Admin Bundle", Arrays.asList(startBundle.getPermissionName(), stopBundle.getPermissionName(), restartBundle.getPermissionName()));

        //initialize startup user
        MotechUser admin = new MotechUserCouchdbImpl("motech", "motech", "1", "", Arrays.asList(adminUser.getRoleName(), adminBundle.getRoleName()));

        //create all startup security
        allMotechPermissions.add(addUserPerrmision);
        allMotechPermissions.add(editUserPermission);
        allMotechPermissions.add(deleteUserPermission);
        allMotechPermissions.add(startBundle);
        allMotechPermissions.add(stopBundle);
        allMotechPermissions.add(restartBundle);
        allMotechPermissions.add(uninstallBundle);
        allMotechPermissions.add(installBundle);
        allMotechPermissions.add(changeConfigBundle);
        allMotechRoles.add(adminUser);
        allMotechRoles.add(adminBundle);
        allMotechUsers.add(admin);
    }
}

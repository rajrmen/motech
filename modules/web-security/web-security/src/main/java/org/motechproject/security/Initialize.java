package org.motechproject.security;

import org.ektorp.CouchDbConnector;
import org.motechproject.security.domain.*;
import org.motechproject.security.repository.AllMotechPermissions;
import org.motechproject.security.repository.AllMotechRoles;
import org.motechproject.security.repository.AllMotechUsers;
import org.motechproject.security.service.MotechUserService;
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
    MotechUserService motechUserService;

    @Autowired
    public void initialize(@Qualifier("webSecurityDbConnector") CouchDbConnector db) {
        //initialize startup permission for admin user
        MotechPermission addUserPerrmision = new MotechPermissionCouchdbImpl("addUser", "websecurity");
        MotechPermission editUserPermission = new MotechPermissionCouchdbImpl("editUser", "websecurity");
        MotechPermission deleteUserPermission = new MotechPermissionCouchdbImpl("deleteUser", "websecurity");

        //initialize startup permission for admin bundle
        MotechPermission startBundle = new MotechPermissionCouchdbImpl("startBundle", "websecurity");
        MotechPermission stopBundle = new MotechPermissionCouchdbImpl("stopBundle", "websecurity");
        MotechPermission restartBundle = new MotechPermissionCouchdbImpl("restartBundle", "websecurity");
        MotechPermission uninstallBundle = new MotechPermissionCouchdbImpl("uninstallBundle", "websecurity");
        MotechPermission installBundle = new MotechPermissionCouchdbImpl("installBundle", "websecurity");
        MotechPermission changeConfigBundle = new MotechPermissionCouchdbImpl("changeCongigBundle", "websecurity");

        //test
        MotechPermission getBundlesPerrmision = new MotechPermissionCouchdbImpl("getBundles", "admin");
        //initialize startup role
        MotechRole adminUser = new MotechRoleCouchdbImpl("Admin User", Arrays.asList(addUserPerrmision.getPermissionName(), editUserPermission.getPermissionName(), deleteUserPermission.getPermissionName()));
        MotechRole adminBundle = new MotechRoleCouchdbImpl("Admin Bundle", Arrays.asList(startBundle.getPermissionName(), stopBundle.getPermissionName(), restartBundle.getPermissionName(), getBundlesPerrmision.getPermissionName()));


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
        motechUserService.register("motech", "motech", "motech@motech", "", Arrays.asList(adminUser.getRoleName(), adminBundle.getRoleName()), true);
    }
}

package org.motechproject.security;

import org.ektorp.CouchDbConnector;
import org.motechproject.security.domain.MotechPermission;
import org.motechproject.security.domain.MotechPermissionCouchdbImpl;
import org.motechproject.security.domain.MotechRole;
import org.motechproject.security.domain.MotechRoleCouchdbImpl;
import org.motechproject.security.repository.AllMotechPermissions;
import org.motechproject.security.repository.AllMotechRoles;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.server.config.settings.MotechSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;


public class Initialize {
    private static final String WEB_SECURITY = "websecurity";

    @Autowired
    private MotechUserService motechUserService;

    @Autowired
    private AllMotechPermissions allMotechPermissions;

    @Autowired
    private AllMotechRoles allMotechRoles;

    @Autowired
    private PlatformSettingsService settingsService;

    @Autowired
    public void initialize(@Qualifier("webSecurityDbConnector") CouchDbConnector db) throws IOException {
        //initialize startup permission for admin user
        MotechPermission addUserPerrmision = new MotechPermissionCouchdbImpl("addUser", WEB_SECURITY);
        MotechPermission editUserPermission = new MotechPermissionCouchdbImpl("editUser", WEB_SECURITY);
        MotechPermission deleteUserPermission = new MotechPermissionCouchdbImpl("deleteUser", WEB_SECURITY);
        MotechPermission manageUserPerrmision = new MotechPermissionCouchdbImpl("manageUser", WEB_SECURITY);
        MotechPermission activeUserPerrmision = new MotechPermissionCouchdbImpl("activateUser", WEB_SECURITY);
        MotechPermission manageRolePermission = new MotechPermissionCouchdbImpl("manageRole", WEB_SECURITY);

        //initialize startup role
        MotechRole adminUser = new MotechRoleCouchdbImpl("Admin User", Arrays.asList(addUserPerrmision.getPermissionName(), editUserPermission.getPermissionName(), deleteUserPermission.getPermissionName(), manageUserPerrmision.getPermissionName(), activeUserPerrmision.getPermissionName(), manageRolePermission.getPermissionName()));

        //create all startup security
        allMotechPermissions.add(addUserPerrmision);
        allMotechPermissions.add(editUserPermission);
        allMotechPermissions.add(deleteUserPermission);
        allMotechPermissions.add(manageUserPerrmision);
        allMotechPermissions.add(activeUserPerrmision);
        allMotechPermissions.add(manageRolePermission);

        allMotechRoles.add(adminUser);

        MotechSettings sysProperties = settingsService.getPlatformSettings();
        if ("repository".equals(sysProperties.getLoginMode()) && null != sysProperties.getAdminLogin() && null != sysProperties.getAdminPassword() && null != sysProperties.getAdminEmail()) {
            String adminName = sysProperties.getAdminLogin();
            String adminPassword = sysProperties.getAdminPassword();
            String adminEmail = sysProperties.getAdminEmail();
            motechUserService.register(adminName, adminPassword, adminEmail, "", Arrays.asList(adminUser.getRoleName()));
            Properties properties = getProperties(sysProperties);
            settingsService.savePlatformSettings(properties);
        }
    }

    private Properties getProperties(MotechSettings settings) {
        Properties properties = new Properties();
        properties.putAll(convertCouchDbProperties(settings.getCouchDBProperties()));
        properties.putAll(settings.getQuartzProperties());
        properties.putAll(settings.getSchedulerProperties());
        properties.putAll(settings.getMetricsProperties());
        properties.put(MotechSettings.LANGUAGE, settings.getLanguage());
        properties.put(MotechSettings.STATUS_MSG_TIMEOUT, settings.getStatusMsgTimeout());
        properties.put(MotechSettings.LOGINMODE, settings.getLoginMode());
        return properties;
    }

    private Properties convertCouchDbProperties(Properties couchDb){
        Properties couchProperties = new Properties();
        couchProperties.put("db.host", couchDb.getProperty("host"));
        couchProperties.put("db.port", couchDb.getProperty("port"));
        couchProperties.put("db.maxConnections", couchDb.getProperty("maxConnections"));
        couchProperties.put("db.connectionTimeout", couchDb.getProperty("connectionTimeout"));
        couchProperties.put("db.socketTimeout", couchDb.getProperty("socketTimeout"));

        return couchProperties;
    }
}

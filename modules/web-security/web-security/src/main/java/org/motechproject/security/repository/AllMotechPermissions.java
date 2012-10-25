package org.motechproject.security.repository;

import org.motechproject.security.domain.MotechPermission;

/**
 * Created with IntelliJ IDEA.
 * User: lukasz
 * Date: 23.10.12
 * Time: 12:55
 * To change this template use File | Settings | File Templates.
 */
public interface AllMotechPermissions {

    void add(MotechPermission role);

    MotechPermission findByPermissionName(String permissionName);
}

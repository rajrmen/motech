package org.motechproject.security.repository;

import org.motechproject.security.domain.MotechPermission;

public interface AllMotechPermissions {

    void add(MotechPermission permission);

    MotechPermission findByPermissionName(String permissionName);
}

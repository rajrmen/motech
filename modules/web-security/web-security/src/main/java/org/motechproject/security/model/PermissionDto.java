package org.motechproject.security.model;

import org.motechproject.security.domain.MotechPermission;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lukasz
 * Date: 26.10.12
 * Time: 16:55
 * To change this template use File | Settings | File Templates.
 */
public class PermissionDto {

    private String permissionName;

    private String bundleName;

    public PermissionDto() {
    }

    public PermissionDto(MotechPermission motechPermission) {
        this.permissionName = motechPermission.getPermissionName();
        this.bundleName = motechPermission.getBundleName();
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getBundleName() {
        return bundleName;
    }

    public void setBundleName(String bundleName) {
        this.bundleName = bundleName;
    }
}

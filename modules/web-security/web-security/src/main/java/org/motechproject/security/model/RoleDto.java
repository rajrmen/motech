package org.motechproject.security.model;

import org.motechproject.security.domain.MotechRole;

import java.util.List;

public class RoleDto {

    private String roleName;

    private List<String> permissionNames;

    public RoleDto() {
    }

    public RoleDto(MotechRole motechRole) {
        this.roleName = motechRole.getRoleName();
        this.permissionNames = motechRole.getPermissionNames();
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<String> getPermissionNames() {
        return permissionNames;
    }

    public void setPermissionNames(List<String> permissionNames) {
        this.permissionNames = permissionNames;
    }
}


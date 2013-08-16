package org.motechproject.security.model;

import org.motechproject.security.domain.MotechRole;

import java.util.ArrayList;
import java.util.List;

public class RoleDto {

    private String roleName;

    private String originalRoleName;

    private List<String> permissionNames;
    
    private boolean isDeletable;

    public RoleDto() {
        this(null, new ArrayList<String>(), false);
    }

    public RoleDto(MotechRole motechRole) {
        this(motechRole.getRoleName(), motechRole.getPermissionNames(), motechRole.isDeletable());
    }

    public RoleDto(String roleName, List<String> permissionNames, boolean isDeletable) {
        this.roleName = roleName;
        this.permissionNames = permissionNames;
        this.originalRoleName = roleName;
        this.isDeletable = isDeletable;
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

    public String getOriginalRoleName() {
        return originalRoleName;
    }

    public void setOriginalRoleName(String originalRoleName) {
        this.originalRoleName = originalRoleName;
    }
    
    public boolean isDeletable() {
        return isDeletable;
    }

    public void setDeletable(boolean isDeletable) {
        this.isDeletable = isDeletable;
    }
}


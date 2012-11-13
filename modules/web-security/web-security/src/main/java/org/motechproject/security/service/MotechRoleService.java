package org.motechproject.security.service;

import org.motechproject.security.model.RoleDto;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface MotechRoleService {

    List<RoleDto> getRoles();

    RoleDto getRole(String roleName);

    void updateRole(RoleDto role);

    void deleteRole(RoleDto role);

    void createRole(RoleDto role);
}

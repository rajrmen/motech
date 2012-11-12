package org.motechproject.security.service;

import org.motechproject.security.model.RoleDto;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface MotechRoleService {

    List<RoleDto> getRoles();
}

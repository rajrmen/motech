package org.motechproject.security.service;

import org.motechproject.security.domain.MotechRole;
import org.motechproject.security.model.RoleDto;
import org.motechproject.security.repository.AllMotechRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MotechRoleServiceImpl implements MotechRoleService {

    @Autowired
    private AllMotechRoles allMotechRoles;

    @Override
    public List<RoleDto> getRoles() {
        List<RoleDto> roles = new ArrayList<>();
        for (MotechRole role : allMotechRoles.getRoles()) {
            roles.add(new RoleDto(role));
        }
        return roles;
    }
}

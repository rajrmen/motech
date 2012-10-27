package org.motechproject.security.service;

import org.motechproject.security.domain.MotechRole;
import org.motechproject.security.domain.MotechRoleCouchdbImpl;
import org.motechproject.security.model.RoleDto;
import org.motechproject.security.repository.AllMotechPermissions;
import org.motechproject.security.repository.AllMotechRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.security.AllPermission;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lukasz
 * Date: 25.10.12
 * Time: 14:20
 * To change this template use File | Settings | File Templates.
 */
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

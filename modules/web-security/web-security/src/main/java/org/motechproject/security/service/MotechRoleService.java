package org.motechproject.security.service;

import org.motechproject.security.domain.MotechRole;
import org.motechproject.security.domain.MotechRoleCouchdbImpl;
import org.motechproject.security.model.RoleDto;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lukasz
 * Date: 25.10.12
 * Time: 14:45
 * To change this template use File | Settings | File Templates.
 */

public interface MotechRoleService {

    List<RoleDto> getRoles();
}

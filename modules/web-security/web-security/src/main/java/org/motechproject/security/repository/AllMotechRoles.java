package org.motechproject.security.repository;

import org.motechproject.security.domain.MotechRole;

/**
 * Created with IntelliJ IDEA.
 * User: lukasz
 * Date: 23.10.12
 * Time: 12:56
 * To change this template use File | Settings | File Templates.
 */
public interface AllMotechRoles {

    void add(MotechRole role);

    MotechRole findByRoleName(String roleName);
}

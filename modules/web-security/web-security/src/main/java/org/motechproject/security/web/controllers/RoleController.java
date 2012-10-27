package org.motechproject.security.web.controllers;

import org.motechproject.security.domain.MotechRoleCouchdbImpl;
import org.motechproject.security.model.RoleDto;
import org.motechproject.security.service.MotechRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lukasz
 * Date: 25.10.12
 * Time: 14:19
 * To change this template use File | Settings | File Templates.
 */

@Controller
public class RoleController {

    @Autowired
    private MotechRoleService motechRoleService;

    @RequestMapping(value = "/roles", method = RequestMethod.GET)
    @ResponseBody
    public List<RoleDto> getRoles() {
        return motechRoleService.getRoles();
    }
}

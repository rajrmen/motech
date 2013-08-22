package org.motechproject.security.web.controllers;

import org.motechproject.security.model.RoleDto;
import org.motechproject.security.service.MotechRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import static java.util.Arrays.asList;

@Controller
public class RoleController {

    @Autowired
    private MotechRoleService motechRoleService;

    @RequestMapping(value = "/roles", method = RequestMethod.GET)
    @ResponseBody
    public List<RoleDto> getRoles() {
        return motechRoleService.getRoles();
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/roles/getrole", method = RequestMethod.POST)
    @ResponseBody
    public RoleDto getRole(@RequestBody String roleName) {
        return motechRoleService.getRole(roleName);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/roles/update", method = RequestMethod.POST)
    public void updateRole(@RequestBody RoleDto role) {
        motechRoleService.updateRole(role);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/roles/delete", method = RequestMethod.POST)
    public void deleteRole(@RequestBody RoleDto role) {
        motechRoleService.deleteRole(role);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/roles/create", method = RequestMethod.POST)
    public void saveRole(@RequestBody RoleDto role) {
        motechRoleService.createRole(role);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/roles/testDelete", method = RequestMethod.GET)
    @ResponseBody
    public String testDeleteRole(@RequestParam String roleName) {
        try {
            RoleDto role = motechRoleService.getRole(roleName);
            if (null == role) {
                return "Role named " + roleName + " never existed.";
            }
            
            motechRoleService.deleteRole(role);
            if (null == motechRoleService.getRole(roleName)) {
                return "Role named " + roleName + " is gone.";
            } else {
                return "Role named " + roleName + " is stil there.";
            }
        } catch (Exception e) {
            return "Error deleting role named " + roleName + ": " + e.toString();
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/roles/testCreate", method = RequestMethod.GET)
    @ResponseBody
    public String testCreateRole(@RequestParam String roleName) {
        try {
            RoleDto newRole = new RoleDto(roleName, asList("addUser", "editUser"), true);
            motechRoleService.createRole(newRole);
            return "Role named " + roleName + " created.";
        } catch (Exception e) {
            return "Error creating role named " + roleName + ": " + e.toString();
        }
    }

}

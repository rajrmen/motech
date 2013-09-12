package org.motechproject.security.web.controllers;

import org.motechproject.security.service.MotechUrlSecurityServiceImpl;
import org.motechproject.security.service.UserAccessServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class TestSecurityController {

    private static final String PATH = "path";

    @Autowired
    private MotechUrlSecurityServiceImpl urlService;

    @Autowired
    private UserAccessServiceImpl userAccessService;

    @RequestMapping(value = "/removeSecurity", method = RequestMethod.GET)
    @ResponseBody
    public String removeSecurity(HttpServletRequest request) {
        String path = request.getParameter(PATH);
        urlService.removeSecurityForPath(path);
        return "Removed security for: " + path;
    }

    @RequestMapping(value = "/removePathSettings", method = RequestMethod.GET)
    @ResponseBody
    public String removePathSettings(HttpServletRequest request) {
        String path = request.getParameter(PATH);
        urlService.removePathFilter(path);
        return "Removed path configuration for: " + path;
    }

    @RequestMapping(value = "/annotatedTest", method = RequestMethod.GET)
    @ResponseBody
    public String tryIt() {
        urlService.annotatedMethod();
        return "Annotation test successful.";
    }

    @RequestMapping(value = "/https/1", method = RequestMethod.GET)
    @ResponseBody
    public String test1() {
        return "test1";
    }

    @RequestMapping(value = "/https/1/2", method = RequestMethod.GET)
    @ResponseBody
    public String test12() {
        return "test12";
    }

    @RequestMapping(value = "/https/1/2/3", method = RequestMethod.GET)
    @ResponseBody
    public String test123(HttpServletRequest request) {
        return "test123";
    }

    @RequestMapping(value = "/changeChannelSecurity", method = RequestMethod.GET)
    @ResponseBody
    public String testTwo(HttpServletRequest request) {
        urlService.secure(request.getParameter(PATH));
        return "Secured: " + request.getParameter(PATH);
    }

    @RequestMapping(value = "/addBasicAuth", method = RequestMethod.GET)
    @ResponseBody
    public String addBasicAuth(HttpServletRequest request) {
        urlService.addBasicAuth(request.getParameter(PATH));
        return "Added basic auth";
    }

    @RequestMapping(value = "/removeBasicAuth", method = RequestMethod.GET)
    @ResponseBody
    public String removeBasicAuth(HttpServletRequest request) {
        urlService.removeBasicAuth();
        return "Removed basic auth, only call this immediately after adding";
    }

    @RequestMapping(value = "/addAccessRight", method = RequestMethod.GET)
    @ResponseBody
    public String addAccessRight(HttpServletRequest request) {
        String accessRight = request.getParameter("accessRight");
        String username = request.getParameter("username");
        userAccessService.addAccess(username, accessRight);
        return "Added " + accessRight + " for user " + username;
    }

    @RequestMapping(value = "/addUserAccess", method = RequestMethod.GET)
    @ResponseBody
    public String addUserAccess(HttpServletRequest request) {
        String path = request.getParameter(PATH);
        String accessRight = request.getParameter("accessRight");

        urlService.addUserPermission(path, accessRight);

        return "Added " + accessRight + " security to path: " + path;
    }

    @RequestMapping(value = "/addRoleAccess", method = RequestMethod.GET)
    @ResponseBody
    public String addRoleAccess(HttpServletRequest request) {
        String path = request.getParameter(PATH);
        String permission = request.getParameter("role");

        urlService.addRoleRequirement(path, permission);

        return "Added " + permission + " security to path: " + path;
    }
}

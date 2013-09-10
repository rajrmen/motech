package org.motechproject.security.web.controllers;

import org.motechproject.security.service.MotechUrlSecurityServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class TestSecurityController {

    @Autowired
    private MotechUrlSecurityServiceImpl urlService;

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
        urlService.secure(request.getParameter("path"));
        return "Secured: " + request.getParameter("path");
    }

    @RequestMapping(value = "/addBasicAuth", method = RequestMethod.GET)
    @ResponseBody
    public String addBasicAuth(HttpServletRequest request) {
        urlService.addBasicAuth(request.getParameter("path"));
        return "Added basic auth";
    }

    @RequestMapping(value = "/removeBasicAuth", method = RequestMethod.GET)
    @ResponseBody
    public String removeBasicAuth(HttpServletRequest request) {
        urlService.removeBasicAuth();
        return "Removed basic auth, only call this immediately after adding";
    }

}

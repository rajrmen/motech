package org.motechproject.security.web.controllers;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.security.domain.MotechSecurityConfiguration;
import org.motechproject.security.helper.MotechProxyManager;
import org.motechproject.security.model.SecurityConfigDto;
import org.motechproject.security.repository.AllMotechSecurityRules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

@Controller
public class TestSecurityController {

    @Autowired
    private EventRelay eventRelay;

    @Autowired
    private MotechProxyManager proxyManager;

    @Autowired
    private AllMotechSecurityRules allSecurityRules;

    @RequestMapping(value = "/buildChain", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void rebuildChain() {
        eventRelay.sendEventMessage(new MotechEvent("rebuildchain"));
    }

    @RequestMapping(value = "/annotatedTest", method = RequestMethod.GET)
    @ResponseBody
    public String tryIt() {
        proxyManager.annotatedMethod();
        return "Annotation test successful.";
    }

    @RequestMapping(value = "/https/1", method = RequestMethod.GET)
    @ResponseBody
    public String test1() {
        return "test1";
    }

    @RequestMapping(value = "/https/1/2")
    @ResponseBody
    public String test12() {
        return "test12";
    }

    @RequestMapping(value = "/https/1/2/3", method = RequestMethod.GET)
    @ResponseBody
    public String test123(HttpServletRequest request) {
        return "test123";
    }
    
    @RequestMapping(value = "/updateSecurityRules", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void updateSecurityRules(@RequestBody SecurityConfigDto securityConfig) {
        MotechSecurityConfiguration dbConfig = new MotechSecurityConfiguration();
        dbConfig.setSecurityRules(securityConfig.getSecurityRules());
        allSecurityRules.add(dbConfig);
    }
}

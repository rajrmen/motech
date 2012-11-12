package org.motechproject.security.web.controllers;

import org.motechproject.security.model.UserDto;
import org.motechproject.security.service.MotechUserProfile;
import org.motechproject.security.service.MotechUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    private MotechUserService motechUserService;

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/users/create", method = RequestMethod.POST)
    public void saveUser(@RequestBody UserDto user) {
        motechUserService.register(user.getUserName(), user.getPassword(), user.getEmail(), "", user.getRoles());
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    @ResponseBody
    public List<MotechUserProfile> getUsers() {
        return motechUserService.getUsers();
    }

    @RequestMapping(value = "/users/login", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void loginUser(String userName, String password) {
        motechUserService.loginUser(userName, password);
    }
}

package org.motechproject.security.web.controllers;

import org.motechproject.security.model.UserDto;
import org.motechproject.security.service.MotechUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lukasz
 * Date: 25.10.12
 * Time: 14:57
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class UserController {

    @Autowired
    private MotechUserService motechUserService;

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/users/create", method = RequestMethod.POST)
    public void saveUser(@RequestBody UserDto user) {
        motechUserService.register(user.getUserName(), user.getPassword(), user.getEmail(), "", user.getRoles());
    }

    @RequestMapping(value = "/users/all", method = RequestMethod.GET)
    @ResponseBody
    public List<UserDto> getUsers() {
        return motechUserService.getUsers();
    }
}

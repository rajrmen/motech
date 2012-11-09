package org.motechproject.security.service;

import org.motechproject.security.model.UserDto;

import java.util.List;


public interface MotechUserService {

    public void register(String username, String password, String email, String externalId, List<String> roles);

    public void register(String username, String password, String email, String externalId, List<String> roles, boolean isActive);

    public void activateUser(String username);

    public MotechUserProfile retrieveUserByCredentials(String username, String password);

    public MotechUserProfile changePassword(String username, String oldPassword, String newPassword);

    public void remove(String username);

    public boolean hasUser(String username);

    public List<UserDto> getUsers();

    public void loginUser(String userName, String password);
}

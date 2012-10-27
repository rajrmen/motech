package org.motechproject.security.domain;

import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public interface MotechUser {

    String getExternalId();

    String getUserName();

    String getPassword();

    String getEmail();

    List<String> getRoles();

    List<GrantedAuthority> getAuthorities();

    boolean isActive();

    void setActive(boolean active);

    void setPassword(String password);

    void setEmail(String email);

    void setUserName(String username);

    void setRoles(List<String> roles);

    void setExternalId(String externalId);
}

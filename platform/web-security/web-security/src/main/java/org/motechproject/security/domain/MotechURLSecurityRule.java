package org.motechproject.security.domain;

import java.util.List;
import java.util.Set;

public interface MotechURLSecurityRule {

    String getPattern();

    void setPattern(String pattern);

    List<String> getSupportedSchemes();

    void setSupportedSchemes(List<String> supportedSchemes);

    String getProtocol();

    void setProtocol(String protocol);

    List<String> getPermissionAccess();

    void setPermissionAccess(List<String> permissionAccess);

    List<String> getUserAccess();

    void setUserAccess(List<String> userAccess);

    int getPriority();

    void setPriority(int priority);

    boolean isRest();

    void setRest(boolean rest);

    String getOrigin();

    void setOrigin(String origin);

    String getVersion();

    void setVersion(String version);

    Set<String> getMethodsRequired();

    void setMethodsRequired(Set<String> methodsRequired);
}

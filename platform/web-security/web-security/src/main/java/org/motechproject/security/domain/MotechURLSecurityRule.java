package org.motechproject.security.domain;

import java.util.List;

public interface MotechURLSecurityRule {

    String getPattern();

    void setPattern(String pattern);

    String getScheme();

    void setScheme(String scheme);

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
}

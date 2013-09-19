package org.motechproject.security.domain;

import java.util.List;

public interface MotechURLSecurityRule {

    public String getPattern();

    public void setPattern(String pattern);

    public String getScheme();

    public void setScheme(String scheme);

    public String getProtocol();

    public void setProtocol(String protocol);

    public List<String> getPermissionAccess();

    public void setPermissionAccess(List<String> permissionAccess);

    public List<String> getUserAccess();

    public void setUserAccess(List<String> userAccess);

    public int getPriority();

    public void setPriority(int priority);

    public boolean isRest();

    public void setRest(boolean rest);

    public String getOrigin();

    public void setOrigin(String origin);

    public String getVersion();

    public void setVersion(String version);
}

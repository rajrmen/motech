package org.motechproject.security.service;

public interface UserAccessService {

    boolean hasAccess(String username, String accessRight);

    void addAccess(String username, String accessRight);

    void removeAccess(String username, String accessRight);

}

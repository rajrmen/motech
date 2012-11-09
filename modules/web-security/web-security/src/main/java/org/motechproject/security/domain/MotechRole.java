package org.motechproject.security.domain;

import java.util.List;

public interface MotechRole {

    String getRoleName();

    List<String> getPermissionNames();
}

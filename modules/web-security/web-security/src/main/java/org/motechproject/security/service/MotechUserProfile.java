package org.motechproject.security.service;

import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.domain.MotechRole;

import java.util.List;

public class MotechUserProfile {

    private MotechUser user;

    public MotechUserProfile(MotechUser motechUser) {
        this.user = motechUser;
    }

    public String getExternalId() {
        return user.getExternalId();
    }

    public String getUserName() {
        return user.getUserName();
    }

    public List<MotechRole> getRoles() {
        return user.getRoles();
    }
}

package org.motechproject.security.service;

import java.util.ArrayList;
import java.util.List;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.repository.AllMotechUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserAccessServiceImpl {

    @Autowired
    private AllMotechUsers allMotechUsers;

    public boolean hasAccess(String username, String accessRight) {
        MotechUser user = allMotechUsers.findByUserName(username);
        if (user == null || user.getAccessRights() == null || !user.getAccessRights().contains(accessRight)) {
            return false;
        }
        return true;
    }

    public void addAccess(String username, String accessRight) {
        MotechUser user = allMotechUsers.findByUserName(username);
        if (user == null) {
            return;
        }
        if (user.getAccessRights() == null) {
            List<String> accessRights = new ArrayList<String>();
            accessRights.add(accessRight);
            user.setAccessRights(accessRights);
            allMotechUsers.update(user);
        } else {
            user.getAccessRights().add(accessRight);
            allMotechUsers.update(user);
        }
    }
}

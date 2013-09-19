package org.motechproject.security.service;

import java.util.ArrayList;
import java.util.List;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.repository.AllMotechUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserAccessServiceImpl implements UserAccessService {

    @Autowired
    private AllMotechUsers allMotechUsers;

    @Override
    public boolean hasAccess(String username, String accessRight) {
        MotechUser user = allMotechUsers.findByUserName(username);
        if (user == null || user.getAccessRights() == null || !user.getAccessRights().contains(accessRight)) {
            return false;
        }
        return true;
    }

    @Override
    public void addAccess(String username, String accessRight) {
        MotechUser user = allMotechUsers.findByUserName(username);
        if (user == null) {
            return;
        }
        List<String> accessRights = user.getAccessRights();

        if (accessRights == null) {
            accessRights = new ArrayList<String>();
            accessRights.add(accessRight);
            user.setAccessRights(accessRights);
            allMotechUsers.update(user);
        } else if (!accessRights.contains(accessRight)){
            user.getAccessRights().add(accessRight);
            allMotechUsers.update(user);
        }
    }

    @Override
    public void removeAccess(String username, String accessRight) {
        MotechUser user = allMotechUsers.findByUserName(username);
        if (user == null) {
            return;
        }
        if (user.getAccessRights() == null || user.getAccessRights().size() == 0) {
            return;
        } else {
            user.getAccessRights().remove(accessRight);
            allMotechUsers.update(user);
        }        
    }
}

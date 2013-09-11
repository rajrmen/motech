package org.motechproject.security.authentication;

import java.util.Collection;
import org.motechproject.security.service.MotechUserProfile;
import org.motechproject.security.service.UserAccessServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class MotechAccessVoter implements AccessDecisionVoter<Object> {

    private String accessPrefix = "ACCESS_";

    @Autowired
    private UserAccessServiceImpl userAccessService;

    @Override
    public boolean supports(ConfigAttribute attribute) {
        if (attribute.getAttribute() != null && attribute.getAttribute().startsWith(accessPrefix)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
        int result = ACCESS_ABSTAIN;

        for (ConfigAttribute attribute : attributes) {
            if (this.supports(attribute)) {
                result = ACCESS_DENIED;

                MotechUserProfile motechProfile = (MotechUserProfile) authentication.getDetails();

                if (userAccessService.hasAccess(motechProfile.getUserName(), attribute.getAttribute())) {
                    return ACCESS_GRANTED;
                }
            }
        }

        return result;
    }


}

package org.motechproject.security.authentication;

import java.util.Collection;

import org.motechproject.security.domain.MotechUserProfile;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

public class MotechAccessVoter implements AccessDecisionVoter<Object> {

    private String accessPrefix = "ACCESS_";
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
        if (!(authentication.getDetails() instanceof MotechUserProfile)) {
            return result;
        }

        for (ConfigAttribute attribute : attributes) {
            if (this.supports(attribute)) {
                result = ACCESS_DENIED;

                MotechUserProfile motechProfile = (MotechUserProfile) authentication.getDetails();

                if (("ACCESS_" + motechProfile.getUserName()).equals(attribute.getAttribute())) {
                    return ACCESS_GRANTED;
                }
            }
        }

        return result;
    }


}

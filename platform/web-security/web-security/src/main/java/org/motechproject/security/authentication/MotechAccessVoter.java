package org.motechproject.security.authentication;

import java.util.Collection;

import org.motechproject.security.domain.MotechUserProfile;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

/**
 * A custom AccessDecisionVoter for voting on whether
 * a specific user has access to a particular URL. For example,
 * a security rule can specify that the users motech and admin
 * have access to a particular URL. This loads the metadata source
 * with attributes for ACCESS_motech and ACCESS_admin. When a user
 * invokes that URL, an affirmative based voting system will check
 * whether or not the user is motech or admin. If not, they are denied
 * permission, otherwise they are granted access.
 *
 */
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

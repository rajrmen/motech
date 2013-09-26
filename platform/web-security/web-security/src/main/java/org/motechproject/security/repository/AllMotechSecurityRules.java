package org.motechproject.security.repository;

import java.util.List;

import org.motechproject.security.domain.MotechSecurityConfiguration;
import org.motechproject.security.domain.MotechURLSecurityRule;

/**
 * 
 * @author Russell
 *
 */
public interface AllMotechSecurityRules {

    void add(MotechSecurityConfiguration config);

    void update(MotechSecurityConfiguration config);

    void remove(MotechSecurityConfiguration config);

    List<MotechURLSecurityRule> getRules();
}

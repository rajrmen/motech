package org.motechproject.security.service;

import java.util.List;

import org.motechproject.security.domain.MotechSecurityConfiguration;
import org.motechproject.security.domain.MotechURLSecurityRule;

/**
 * 
 * @author Russell
 *
 */
public interface MotechURLSecurityService {

    List<MotechURLSecurityRule> findAllSecurityRules();
    
    void updateSecurityConfiguration(MotechSecurityConfiguration configuration);

}

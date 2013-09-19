package org.motechproject.security.service;

import java.util.List;

import org.motechproject.security.domain.MotechURLSecurityRule;

public interface MotechURLSecurityService {

    void addNewSecurityRule(MotechURLSecurityRule rule);

    void updateSecurityRule(MotechURLSecurityRule rule);

    void removeSecurityRule(MotechURLSecurityRule rule);

    List<MotechURLSecurityRule> findAllSecurityRules();

}

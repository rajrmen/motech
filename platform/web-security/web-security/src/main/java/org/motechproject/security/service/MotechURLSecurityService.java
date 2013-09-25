package org.motechproject.security.service;

import java.util.List;

import org.motechproject.security.domain.MotechURLSecurityRule;

public interface MotechURLSecurityService {

    List<MotechURLSecurityRule> findAllSecurityRules();

}

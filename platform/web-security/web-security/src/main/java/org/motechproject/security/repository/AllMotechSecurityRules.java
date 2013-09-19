package org.motechproject.security.repository;

import java.util.List;

import org.motechproject.security.domain.MotechURLSecurityRule;

public interface AllMotechSecurityRules {

    void add(MotechURLSecurityRule rule);

    void update(MotechURLSecurityRule rule);

    void remove(MotechURLSecurityRule rule);

    List<MotechURLSecurityRule> getRules();

    List<MotechURLSecurityRule> findByModule(String module);

    MotechURLSecurityRule findByModuleAndPattern(String module, String pattern);
}

package org.motechproject.security.model;

import java.util.List;

import org.motechproject.security.domain.MotechURLSecurityRule;

public class SecurityConfigDto {

    private List<MotechURLSecurityRule> securityRules;

    public List<MotechURLSecurityRule> getSecurityRules() {
        return securityRules;
    }

    public void setSecurityRules(List<MotechURLSecurityRule> securityRules) {
        this.securityRules = securityRules;
    }
}

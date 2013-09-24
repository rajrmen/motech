package org.motechproject.security.model;

import java.util.List;

public class SecurityConfigDto {

    private List<SecurityRuleDto> securityRules;

    public List<SecurityRuleDto> getSecurityRules() {
        return securityRules;
    }

    public void setSecurityRules(List<SecurityRuleDto> securityRules) {
        this.securityRules = securityRules;
    }
}

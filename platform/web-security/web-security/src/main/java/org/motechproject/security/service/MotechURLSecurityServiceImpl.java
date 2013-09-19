package org.motechproject.security.service;

import java.util.List;

import org.motechproject.security.domain.MotechURLSecurityRule;
import org.motechproject.security.repository.AllMotechSecurityRules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("motechURLSecurityService")
public class MotechURLSecurityServiceImpl implements MotechURLSecurityService {

    @Autowired
    private AllMotechSecurityRules allSecurityRules;
    
    @Override
    public void addNewSecurityRule(MotechURLSecurityRule rule) {
        MotechURLSecurityRule oldRule = allSecurityRules.findByModuleAndPattern(rule.getOrigin(), rule.getPattern());
        if (oldRule != null) {
            return;
        }
        allSecurityRules.add(rule);
    }

    @Override
    public void updateSecurityRule(MotechURLSecurityRule rule) {
        allSecurityRules.update(rule);
    }

    @Override
    public void removeSecurityRule(MotechURLSecurityRule rule) {
        allSecurityRules.remove(rule);
    }

    @Override
    public List<MotechURLSecurityRule> findAllSecurityRules() {
        return allSecurityRules.getRules();
    }

}

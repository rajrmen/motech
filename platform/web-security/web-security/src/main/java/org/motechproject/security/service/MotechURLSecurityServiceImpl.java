package org.motechproject.security.service;

import java.util.List;

import org.motechproject.security.domain.MotechSecurityConfiguration;
import org.motechproject.security.domain.MotechURLSecurityRule;
import org.motechproject.security.helper.MotechProxyManager;
import org.motechproject.security.repository.AllMotechSecurityRules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service("motechURLSecurityService")
public class MotechURLSecurityServiceImpl implements MotechURLSecurityService {

    @Autowired
    private AllMotechSecurityRules allSecurityRules;

    @Autowired
    private MotechProxyManager proxyManager;

    @Override
    @PreAuthorize("hasAnyRole('viewSecurity', 'updateSecurity')")
    public List<MotechURLSecurityRule> findAllSecurityRules() {
        return allSecurityRules.getRules();
    }

    @Override
    @PreAuthorize("hasRole('updateSecurity')")
    public void updateSecurityConfiguration(MotechSecurityConfiguration configuration) {
        allSecurityRules.add(configuration);
        proxyManager.rebuildProxyChain();
    }
}

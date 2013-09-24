package org.motechproject.security.helper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.security.domain.MotechURLSecurityRule;
import org.motechproject.security.service.MotechURLSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

@Component
public class MotechProxyManager {

    @Autowired
    private FilterChainProxy proxy;

    public FilterChainProxy getFilterChainProxy() {
        return proxy;
    }

    public void setFilterChainProxy(FilterChainProxy proxy) {
        this.proxy = proxy;
    }

    public List<DefaultSecurityFilterChain> getDefaultSecurityChains() {
        List<DefaultSecurityFilterChain> defaultSecurityChains = new ArrayList<DefaultSecurityFilterChain>();
        for (SecurityFilterChain chain : proxy.getFilterChains()) {
            defaultSecurityChains.add((DefaultSecurityFilterChain) chain);
        }
        return defaultSecurityChains;
    }

    @Autowired
    private SecurityRuleBuilder securityRuleBuilder;

    @Autowired
    private MotechURLSecurityService motechSecurityService;

    /**
     * Test method for testing permissions on a method
     */
    @PreAuthorize("hasRole('stopBundle')")
    public void annotatedMethod() {
        return;
    }

    @MotechListener(subjects = "rebuildchain")
    public synchronized void rebuildProxyChain(MotechEvent event) {
        List<MotechURLSecurityRule> allSecurityRules = motechSecurityService.findAllSecurityRules();

        List<SecurityFilterChain> newFilterChains = new ArrayList<SecurityFilterChain>();

        for (MotechURLSecurityRule securityRule : allSecurityRules) {
            newFilterChains.add(securityRuleBuilder.buildSecurityChain(securityRule));
        }

        proxy = new FilterChainProxy(newFilterChains);
    }

    private class MotechSecurityRuleComparator implements Comparator<MotechURLSecurityRule> {

        @Override
        public int compare(MotechURLSecurityRule rule1, MotechURLSecurityRule rule2) {
            if (rule1.getPriority() == rule2.getPriority()) {
                return rule1.getPattern().compareTo(rule2.getPattern());
            } else {
                return rule1.getPriority() - rule2.getPriority();
            }
        }
    }
}

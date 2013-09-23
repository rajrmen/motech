package org.motechproject.security.helper;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.Filter;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.security.domain.MotechURLSecurityRule;
import org.motechproject.security.service.MotechURLSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.AntPathRequestMatcher;
import org.springframework.security.web.util.RequestMatcher;
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

    public void removePathFilter(String path) {
        removeAllSecurity(path, true);
    }

    public void removeSecurityForPath(String path) {
        removeAllSecurity(path, false);
    }

    public void removeAllSecurity(String path, boolean removePath) {
        List<DefaultSecurityFilterChain> filterChains = getDefaultSecurityChains();
        List<SecurityFilterChain> newFilterChains = new ArrayList<SecurityFilterChain>();
        for (DefaultSecurityFilterChain chain : filterChains) {
            RequestMatcher matcher = chain.getRequestMatcher();
            if (matcher instanceof AntPathRequestMatcher) {
                AntPathRequestMatcher antMatcher = (AntPathRequestMatcher) matcher;
                String pattern = antMatcher.getPattern();
                if (!pattern.equals(path)) {
                    newFilterChains.add(chain);
                } else {
                    if (!removePath) {
                        DefaultSecurityFilterChain strippedChain = new DefaultSecurityFilterChain(matcher, new ArrayList<Filter>());
                        newFilterChains.add(strippedChain);
                    }
                }
            } else {
                newFilterChains.add(chain);
            }
        }

        proxy = new FilterChainProxy(newFilterChains);
    }

    @MotechListener(subjects = "rebuildchain")
    public void rebuildProxyChain(MotechEvent event) {
        List<MotechURLSecurityRule> allSecurityRules = motechSecurityService.findAllSecurityRules();

        List<SecurityFilterChain> newFilterChains = new ArrayList<SecurityFilterChain>();

        for (MotechURLSecurityRule securityRule : allSecurityRules) {
            newFilterChains.add(securityRuleBuilder.buildSecurityChain(securityRule));
        }

        proxy = new FilterChainProxy(newFilterChains);
    }

}

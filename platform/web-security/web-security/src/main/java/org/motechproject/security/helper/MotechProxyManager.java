package org.motechproject.security.helper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
}

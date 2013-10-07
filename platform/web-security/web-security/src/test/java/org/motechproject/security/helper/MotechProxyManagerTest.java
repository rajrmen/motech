package org.motechproject.security.helper;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:META-INF/motech/*.xml")
public class MotechProxyManagerTest {

    @Autowired
    private MotechProxyManager proxyManager;
    
    @Test
    public void testProxyHasDefaultSecurityChains() {
        FilterChainProxy filterChainProxy = proxyManager.getFilterChainProxy();
        List<SecurityFilterChain> filterChains = filterChainProxy.getFilterChains();
        
        for (SecurityFilterChain filterChain: filterChains) {
            
        }
    }
}

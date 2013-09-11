package org.motechproject.security.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.Filter;
import org.motechproject.security.authentication.MotechAccessVoter;
import org.motechproject.security.filter.MotechChannelProcessingFilter;
import org.motechproject.security.helper.MotechProxyManager;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelDecisionManager;
import org.springframework.security.web.access.intercept.DefaultFilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.util.AntPathRequestMatcher;
import org.springframework.security.web.util.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
public class MotechUrlSecurityServiceImpl {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ChannelDecisionManager manager;

    @Autowired
    private BasicAuthenticationFilter basicAuth;

    @Autowired
    private BeanFactory beanFactory;

    @Autowired
    private WebApplicationContext webContext;

    @Autowired
    private MotechProxyManager proxyManager;
    
    @Autowired
    private MotechAccessVoter accessVoter;

    public void factory() {
        beanFactory.getBean("any");
        webContext.containsBean("blah");
    }

    /**
     * Secures given path with HTTPS (only one path right now, reset at each invocation)
     */
    public void secure(String path) {
        List<SecurityFilterChain> chains = proxyManager.getFilterChainProxy().getFilterChains();

        List<SecurityFilterChain> newChains = new ArrayList<SecurityFilterChain>();
        newChains.addAll(chains);

        for (SecurityFilterChain chain : newChains) {

            for (Filter targetFilter : chain.getFilters()) {
                if (targetFilter instanceof MotechChannelProcessingFilter) {

                    LinkedHashMap<RequestMatcher, Collection<ConfigAttribute>> requestMap = new LinkedHashMap<RequestMatcher, Collection<ConfigAttribute>>();

                    RequestMatcher matcherOne = new AntPathRequestMatcher("/**");
                    RequestMatcher matcherTwo = new AntPathRequestMatcher("/**/" + path + "/**");

                    Collection<ConfigAttribute> collectionOne = new ArrayList<ConfigAttribute>();
                    Collection<ConfigAttribute> collectionTwo = new ArrayList<ConfigAttribute>();

                    collectionOne.add(new SecurityConfig("ANY_CHANNEL"));
                    collectionTwo.add(new SecurityConfig("REQUIRES_SECURE_CHANNEL"));

                    requestMap.put(matcherTwo, collectionTwo);
                    requestMap.put(matcherOne, collectionOne);

                    FilterInvocationSecurityMetadataSource newSource = new DefaultFilterInvocationSecurityMetadataSource(requestMap);

                    MotechChannelProcessingFilter newFilter = new MotechChannelProcessingFilter();
                    newFilter.setSecurityMetadataSource(newSource);
                    newFilter.setChannelDecisionManager(manager);

                    chain.getFilters().add(0, newFilter);
                    chain.getFilters().remove(targetFilter);

                    proxyManager.setFilterChainProxy(new FilterChainProxy(newChains));

                    return;
                }
            }
        }
    }

    /**
     * Test method for testing permissions on a method
     */
    @PreAuthorize("hasRole('stopBundle')")
    public void annotatedMethod() {
        return;
    }

    /**
     * Add permission requirement to a URL path
     */

    public void addUserPermission(String path, String permission) {
        List<String> filtersToAdd = new ArrayList<String>();
        filtersToAdd.add("userFilter");
        setupChain(path, filtersToAdd, permission);
    }

    public void addRoleRequirement(String path, String permission) {
        List<String> filtersToAdd = new ArrayList<String>();
        filtersToAdd.add("roleFilter");
        setupChain(path, filtersToAdd, permission);
    }

    /**
     * Add basic auth to a URL path
     */
    public void addBasicAuth(String path) {
        List<String> filtersToAdd = new ArrayList<String>();
        filtersToAdd.add("basicFilter");
        setupChain(path, filtersToAdd, null);
    }

    /**
     * Set up the new security filter chains and reconstruct the proxy
     */
    private void setupChain(String path, List<String> filtersToAdd, String role) {
        List<SecurityFilterChain> newChains = new ArrayList<SecurityFilterChain>();
        newChains.addAll(proxyManager.getFilterChainProxy().getFilterChains());

        List<Filter> filters = new ArrayList<Filter>();

        Filter filterOne = new SecurityContextPersistenceFilter();
        filters.add(filterOne);

        Map<RequestMatcher, Collection<ConfigAttribute>> requestMap = new LinkedHashMap<RequestMatcher, Collection<ConfigAttribute>>();

        RequestMatcher matcherTwo = new AntPathRequestMatcher("/**/" + path + "/**");
        Collection<ConfigAttribute> collectionTwo = new ArrayList<ConfigAttribute>();

        if (filtersToAdd.contains("basicFilter")) {
            filters.add(basicAuth);
        }

        if (filtersToAdd.contains("roleFilter")) {
            collectionTwo.add(new SecurityConfig(role));
            requestMap.put(matcherTwo, collectionTwo);
            addFilterSecurityInterceptor(filters, requestMap);
        }

        if (filtersToAdd.contains("userFilter")) {
            collectionTwo.add(new SecurityConfig("ACCESS_" + role));
            requestMap.put(matcherTwo, collectionTwo);
            addFilterSecurityInterceptor(filters, requestMap);
        }

        SecurityFilterChain newChain = new DefaultSecurityFilterChain(matcherTwo, filters);

        newChains.add(newChains.size() - 2, newChain );

        proxyManager.setFilterChainProxy(new FilterChainProxy(newChains));
    }

    /**
     * Filter interceptor for voting on role access. Prefix is set to "" 
     * because our permissions don't start with the default ROLE_
     */
    private void addFilterSecurityInterceptor(List<Filter> filters, Map<RequestMatcher, Collection<ConfigAttribute>> requestMap) {
        FilterInvocationSecurityMetadataSource newSource = new DefaultFilterInvocationSecurityMetadataSource((LinkedHashMap<RequestMatcher, Collection<ConfigAttribute>>) requestMap);

        FilterSecurityInterceptor interceptor = new FilterSecurityInterceptor();
        interceptor.setSecurityMetadataSource(newSource);

        List<AccessDecisionVoter> voters = new ArrayList<AccessDecisionVoter>();

        RoleVoter roleVoter = new RoleVoter();

        roleVoter.setRolePrefix("");
        voters.add(roleVoter);

        voters.add(accessVoter);

        AccessDecisionManager decisionManager = new AffirmativeBased(voters);

        interceptor.setAccessDecisionManager(decisionManager);
        interceptor.setAuthenticationManager(authenticationManager);

        filters.add(interceptor);
    }

    /**
     * Test method that shouldn't currently be used
     */
    public void removeBasicAuth() {
        List<SecurityFilterChain> newChains = new ArrayList<SecurityFilterChain>();
        newChains.addAll(proxyManager.getFilterChainProxy().getFilterChains());

        SecurityFilterChain chain = newChains.get(newChains.size() - 3);

        Filter filter = chain.getFilters().get(1);
        chain.getFilters().remove(filter);

        proxyManager.setFilterChainProxy(new FilterChainProxy(newChains));

    }
}

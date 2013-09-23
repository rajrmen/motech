package org.motechproject.security.helper;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.Filter;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.security.authentication.MotechAccessVoter;
import org.motechproject.security.domain.MotechURLSecurityRule;
import org.motechproject.security.filter.MotechChannelProcessingFilter;
import org.motechproject.security.service.MotechURLSecurityService;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.access.channel.ChannelDecisionManager;
import org.springframework.security.web.access.intercept.DefaultFilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCacheAwareFilter;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;
import org.springframework.security.web.util.AntPathRequestMatcher;
import org.springframework.security.web.util.AnyRequestMatcher;
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


    private static final String BASIC_FILTER = "basicFilter";

    @Autowired
    @Qualifier("basicAuthenticationEntryPoint")
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ChannelDecisionManager manager;

    @Autowired
    private MotechAccessVoter accessVoter;

    @Autowired
    private SettingsFacade settingsFacade;

    @Autowired
    private SecurityRuleBuilder securityRuleBuilder;

    @Autowired
    private MotechURLSecurityService motechSecurityService;
    /**
     * Secures given path with HTTPS (only one path right now, reset at each invocation)
     */
    public void secureWithHttps(String path) {
        List<SecurityFilterChain> chains = proxy.getFilterChains();

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

                    proxy = new FilterChainProxy(newChains);

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
        filtersToAdd.add(BASIC_FILTER);
        setupChain(path, filtersToAdd, null);
    }

    /**
     * Set up the new security filter chains and reconstruct the proxy
     */
    private void setupChain(String path, List<String> filtersToAdd, String role) {
        List<SecurityFilterChain> newChains = new ArrayList<SecurityFilterChain>();
        newChains.addAll(proxy.getFilterChains());

        List<Filter> filters = new ArrayList<Filter>();

        Filter filterOne = new SecurityContextPersistenceFilter();
        filters.add(filterOne);

        Map<RequestMatcher, Collection<ConfigAttribute>> requestMap = new LinkedHashMap<RequestMatcher, Collection<ConfigAttribute>>();

        RequestMatcher matcherTwo = new AntPathRequestMatcher("/**/" + path + "/**");
        Collection<ConfigAttribute> collectionTwo = new ArrayList<ConfigAttribute>();

        if (filtersToAdd.contains(BASIC_FILTER)) {
            MotechRestBasicAuthenticationEntryPoint restAuthPoint = new MotechRestBasicAuthenticationEntryPoint(settingsFacade);
            BasicAuthenticationFilter basicAuthFilter = new BasicAuthenticationFilter(authenticationManager, restAuthPoint);
            filters.add(basicAuthFilter);
        }

        this.addRequestCacheFilter(filters);
        this.addSecurityContextHolderAwareRequestFilter(filters);

        if (filtersToAdd.contains(BASIC_FILTER)) {
            SecureRandom random = new SecureRandom();
            AnonymousAuthenticationFilter anonFilter = new AnonymousAuthenticationFilter(Long.toString(random.nextLong()));
            filters.add(anonFilter);
        }

        ExceptionTranslationFilter exceptionFilter = new ExceptionTranslationFilter(authenticationEntryPoint);

        filters.add(exceptionFilter);

        if (filtersToAdd.contains("roleFilter")) {
            collectionTwo.add(new SecurityConfig(role));
            requestMap.put(matcherTwo, collectionTwo);
            addFilterSecurityInterceptor(filters, requestMap);
        }

        if (filtersToAdd.contains(BASIC_FILTER)) {
            collectionTwo.add(new SecurityConfig("IS_AUTHENTICATED_FULLY"));
            requestMap.put(new AnyRequestMatcher(), collectionTwo);
            addFilterSecurityInterceptor(filters, requestMap);
        }

        if (filtersToAdd.contains("userFilter")) {
            collectionTwo.add(new SecurityConfig("ACCESS_" + role));
            requestMap.put(matcherTwo, collectionTwo);
            addFilterSecurityInterceptor(filters, requestMap);
        }

        SecurityFilterChain newChain = new DefaultSecurityFilterChain(matcherTwo, filters);

        newChains.add(newChains.size() - 2, newChain );

        proxy = new FilterChainProxy(newChains);
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

        AuthenticatedVoter authVoter = new AuthenticatedVoter();

        voters.add(authVoter);

        RoleVoter roleVoter = new RoleVoter();

        roleVoter.setRolePrefix("");
        voters.add(roleVoter);

        voters.add(accessVoter);

        AccessDecisionManager decisionManager = new AffirmativeBased(voters);

        interceptor.setAccessDecisionManager(decisionManager);
        interceptor.setAuthenticationManager(authenticationManager);

        filters.add(interceptor);
    }

    private void addRequestCacheFilter(List<Filter> filters) {
        HttpSessionRequestCache sessionRequestCache = new HttpSessionRequestCache();
        RequestCacheAwareFilter cacheFilter = new RequestCacheAwareFilter(sessionRequestCache);
        filters.add(cacheFilter);
    }

    private void addSecurityContextHolderAwareRequestFilter(List<Filter> filters) {
        SecurityContextHolderAwareRequestFilter securityFilter = new SecurityContextHolderAwareRequestFilter();
        filters.add(securityFilter);
    }

    /**
     * Test method that shouldn't currently be used
     */
    public void removeBasicAuth() {
        List<SecurityFilterChain> newChains = new ArrayList<SecurityFilterChain>();
        newChains.addAll(proxy.getFilterChains());

        SecurityFilterChain chain = newChains.get(newChains.size() - 3);

        Filter filter = chain.getFilters().get(1);
        chain.getFilters().remove(filter);

        proxy = new FilterChainProxy(newChains);

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
    public void rebuildProxyChain() {
        List<MotechURLSecurityRule> allSecurityRules = motechSecurityService.findAllSecurityRules();

        List<SecurityFilterChain> newFilterChains = new ArrayList<SecurityFilterChain>();

        for (MotechURLSecurityRule securityRule : allSecurityRules) {
            newFilterChains.add(securityRuleBuilder.buildSecurityChain(securityRule));
        }
    }

}

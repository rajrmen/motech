package org.motechproject.security.helper;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;

import org.apache.commons.collections.CollectionUtils;
import org.motechproject.security.authentication.MotechAccessVoter;
import org.motechproject.security.domain.MotechURLSecurityRule;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelDecisionManager;
import org.springframework.security.web.util.AntPathRequestMatcher;
import org.springframework.security.web.util.AnyRequestMatcher;
import org.springframework.security.web.util.RequestMatcher;
import org.springframework.stereotype.Component;

@Component
public class SecurityRuleBuilder {

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

    public synchronized SecurityFilterChain buildSecurityChain(MotechURLSecurityRule securityRule) {
        List<Filter> filters = new ArrayList<Filter>();
        RequestMatcher matcher;

        String pattern = securityRule.getPattern();

        if (pattern.equals("ANY") || pattern.equals("/**")) {
            matcher = new AnyRequestMatcher();
        } else {
            matcher = new AntPathRequestMatcher(pattern);
        }

        if (!noSecurity(securityRule)) {
            filters = addFilters(securityRule);
        }

        return new DefaultSecurityFilterChain(matcher, filters);
    }

    private static boolean noSecurity(MotechURLSecurityRule securityRule) {

        if (!securityRule.getProtocol().equals("HTTP")) {
            return false;
        }
        if (securityRule.getScheme().equals("NONE")) {
            return false;
        }
        if (!CollectionUtils.isEmpty(securityRule.getPermissionAccess())) {
            return false;
        }
        if (!CollectionUtils.isEmpty(securityRule.getUserAccess())) {
            return false;
        }

        return true;
    }

    private static List<Filter> addFilters(MotechURLSecurityRule securityRule) {
        List<Filter> filters = new ArrayList<Filter>();

        addSecureChannel(filters, securityRule.getProtocol());
        addSecurityContextPersistenceFilter(filters);
        addAuthenticationFilters(filters, securityRule);
        addRequestCacheFilter(filters);
        addSecurityContextHolderAwareRequestFilter(filters);
        addAnonymousAuthenticationFilter(filters);
        addExceptionTranslationFilter(filters);
        addFilterSecurityInterceptor(filters, securityRule);

        return filters;
    }

    private static void addSecurityContextHolderAwareRequestFilter(List<Filter> filters) {
        // TODO Auto-generated method stub

    }

    private static void addRequestCacheFilter(List<Filter> filters) {
        // TODO Auto-generated method stub

    }

    private static void addFilterSecurityInterceptor(List<Filter> filters, MotechURLSecurityRule securityRule) {
        // TODO Auto-generated method stub

    }

    private static void addExceptionTranslationFilter(List<Filter> filters) {
        // TODO Auto-generated method stub

    }

    private static void addAnonymousAuthenticationFilter(List<Filter> filters) {
        // TODO Auto-generated method stub

    }

    private static void addAuthenticationFilters(List<Filter> filters, MotechURLSecurityRule securityRule) {
        if (securityRule.isRest()) {
            //basicfilter
        } else {
            //usernamepasswordfilter + basicfilter
        }
    }

    private static void addSecurityContextPersistenceFilter(List<Filter> filters) {
        // TODO Auto-generated method stub

    }

    private static void addSecureChannel(List<Filter> filters, String protocol) {
        // TODO Auto-generated method stub

    }



}

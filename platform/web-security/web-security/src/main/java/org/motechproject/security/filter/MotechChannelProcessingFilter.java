package org.motechproject.security.filter;

import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;

/**
 * Custom Channel processing filter that exists solely
 * to obtain security metadata source from the filter
 */
public class MotechChannelProcessingFilter extends ChannelProcessingFilter {

    public FilterInvocationSecurityMetadataSource getSecurityMetadataSource() {
        return super.getSecurityMetadataSource();
    }
}

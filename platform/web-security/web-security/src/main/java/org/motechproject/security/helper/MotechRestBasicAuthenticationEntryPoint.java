package org.motechproject.security.helper;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.util.Assert;

public class MotechRestBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

    public static final String SECURITY_REALM_KEY = "security.realm";

    public MotechRestBasicAuthenticationEntryPoint(SettingsFacade settingsFacade) {
        String realmName = settingsFacade.getProperty(SECURITY_REALM_KEY);
        Assert.hasText(realmName, "realmName must be specified");
        setRealmName(realmName);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}

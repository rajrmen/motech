package org.motechproject.security.osgi;

import static org.osgi.framework.Bundle.ACTIVE;
import static org.osgi.framework.Bundle.UNINSTALLED;
import static org.osgi.framework.Bundle.RESOLVED;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.motechproject.security.domain.MotechSecurityConfiguration;
import org.motechproject.security.domain.MotechURLSecurityRule;
import org.motechproject.security.helper.MotechProxyManager;
import org.motechproject.security.model.PermissionDto;
import org.motechproject.security.model.RoleDto;
import org.motechproject.security.osgi.helper.SecurityTestConfigBuilder;
import org.motechproject.security.repository.AllMotechSecurityRules;
import org.motechproject.security.service.MotechPermissionService;
import org.motechproject.security.service.MotechRoleService;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.motechproject.testing.utils.PollingHttpClient;
import org.motechproject.testing.utils.TestContext;
import org.motechproject.testing.utils.Wait;
import org.motechproject.testing.utils.WaitCondition;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.web.context.WebApplicationContext;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class WebSecurityBundleIT extends BaseOsgiIT {
    private static final Integer TRIES_COUNT = 100;
    private static final String PERMISSION_NAME = "test-permission";
    private static final String ROLE_NAME = "test-role";
    private static final String USER_NAME = "test-username";
    private static final String USER_PASSWORD = "test-password";
    private static final String USER_EMAIL = "test@email.com";
    private static final String USER_EXTERNAL_ID = "test-externalId";
    private static final Locale USER_LOCALE = Locale.ENGLISH;
    private static final String SECURITY_BUNDLE_NAME = "motech-platform-web-security";

    private PollingHttpClient httpClient = new PollingHttpClient(new DefaultHttpClient(), 60);

    public void testDynamicSecurity() throws InterruptedException, IOException, BundleException {
        HttpGet httpGet = new HttpGet(String.format("http://localhost:%d/websecurity/api/web-api/status", TestContext.getJettyPort()));
        addAuthHeader(httpGet, "motech", "motech");

        HttpResponse response = httpClient.execute(httpGet);

        assertNotNull(response);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

        updateSecurity(SecurityTestConfigBuilder.buildConfig("removeAccess"));

        restartSecurityBundle();

        httpGet = new HttpGet(String.format("http://localhost:%d/websecurity/api/web-api/status", TestContext.getJettyPort()));
        addAuthHeader(httpGet, "motech", "motech");

        response = httpClient.execute(httpGet);

        assertNotNull(response);
        assertEquals(HttpStatus.SC_FORBIDDEN, response.getStatusLine().getStatusCode());

    }

    public void testWebSecurityServices() throws Exception {
        // given
        MotechPermissionService permissions = getService(MotechPermissionService.class);
        MotechRoleService roles = getService(MotechRoleService.class);
        MotechUserService users = getService(MotechUserService.class);

        PermissionDto permission = new PermissionDto(PERMISSION_NAME);
        RoleDto role = new RoleDto(ROLE_NAME, Arrays.asList(PERMISSION_NAME));

        // when
        permissions.addPermission(permission);
        roles.createRole(role);
        users.register(USER_NAME, USER_PASSWORD, USER_EMAIL, USER_EXTERNAL_ID, Arrays.asList(ROLE_NAME), USER_LOCALE);

        // then
        assertTrue(String.format("Permission %s has not been saved", PERMISSION_NAME), permissions.getPermissions().contains(permission));
        assertEquals(String.format("Role %s has not been saved properly", ROLE_NAME), role, roles.getRole(ROLE_NAME));
        assertNotNull(String.format("User %s has not been registered", USER_NAME), users.hasUser(USER_NAME));
        assertTrue(String.format("User doesn't have role %s", ROLE_NAME), users.getRoles(USER_NAME).contains(ROLE_NAME));
    }

    public void testProxyInitialization() throws Exception {
        WebApplicationContext theContext = getService(WebApplicationContext.class);
        MotechProxyManager manager = theContext.getBean(MotechProxyManager.class);
        FilterChainProxy proxy = manager.getFilterChainProxy();
        assertNotNull(proxy);
        assertNotNull(proxy.getFilterChains());
    }

    public void testUpdatingProxy() throws InterruptedException, BundleException, IOException, ClassNotFoundException {

        MotechSecurityConfiguration config = SecurityTestConfigBuilder.buildConfig("noSecurity");
        updateSecurity(config);

        restartSecurityBundle();

        MotechProxyManager manager = getProxyManager();
        assertTrue(manager.getFilterChainProxy().getFilterChains().size() == 1);

        MotechSecurityConfiguration updatedConfig = SecurityTestConfigBuilder.buildConfig("userAccess");
        updateSecurity(updatedConfig);

        restartSecurityBundle();

        manager = getProxyManager();
        assertTrue(manager.getFilterChainProxy().getFilterChains().size() == 2);
    }

    private <T> T getService(Class<T> clazz) throws InterruptedException {
        T service = clazz.cast(bundleContext.getService(getServiceReference(clazz)));

        assertNotNull(String.format("Service %s is not available", clazz.getName()), service);

        return service;
    }

    private <T> ServiceReference getServiceReference(Class<T> clazz) throws InterruptedException {
        ServiceReference serviceReference;
        int tries = 0;

        do {
            serviceReference = bundleContext.getServiceReference(clazz.getName());
            ++tries;
            Thread.sleep(2000);
        } while (serviceReference == null && tries < TRIES_COUNT);

        assertNotNull(String.format("Not found service reference for %s", clazz.getName()), serviceReference);

        return serviceReference;
    }

    private void addAuthHeader(HttpGet httpGet, String userName, String password) {
        httpGet.addHeader("Authorization", "Basic " + new String(Base64.encodeBase64((userName + ":" + password).getBytes())));
    }

    private Bundle getBundle(String symbolicName) {
        Bundle testBundle = null;
        for (Bundle bundle : bundleContext.getBundles()) {
            if (null != bundle.getSymbolicName() && bundle.getSymbolicName().contains(symbolicName)
                    && UNINSTALLED != bundle.getState()) {
                testBundle = bundle;
                break;
            }
        }
        assertNotNull(testBundle);
        return testBundle;
    }

    private void waitForBundleState(final Bundle bundle, final int state) throws InterruptedException {
        new Wait(new WaitCondition() {
            @Override
            public boolean needsToWait() {
                return state == bundle.getState();
            }
        }, 2000).start();
        assertEquals(state, bundle.getState());
    }

    private void updateSecurity(MotechSecurityConfiguration config) throws InterruptedException {
        WebApplicationContext theContext = getService(WebApplicationContext.class);
        AllMotechSecurityRules allSecurityRules = theContext.getBean(AllMotechSecurityRules.class);
        allSecurityRules.add(config);
    }

    private List<MotechURLSecurityRule> getRules() throws InterruptedException {
        WebApplicationContext theContext = getService(WebApplicationContext.class);
        AllMotechSecurityRules allSecurityRules = theContext.getBean(AllMotechSecurityRules.class);
        return allSecurityRules.getRules();
    }

    private MotechProxyManager getProxyManager() throws InterruptedException {
        WebApplicationContext theContext = getService(WebApplicationContext.class);
        return theContext.getBean(MotechProxyManager.class);
    }

    private void restartSecurityBundle() throws BundleException, InterruptedException, IOException {
        Bundle securityBundle = getBundle(SECURITY_BUNDLE_NAME);
        securityBundle.stop();
        waitForBundleState(securityBundle, RESOLVED);
        securityBundle.start();
        waitForBundleState(securityBundle, ACTIVE);
    }
}

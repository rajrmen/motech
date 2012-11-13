package org.motechproject.security.authentication;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.domain.MotechUserCouchdbImpl;
import org.motechproject.security.repository.AllMotechRoles;
import org.motechproject.security.repository.AllMotechUsers;
import org.motechproject.security.service.MotechUserProfile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MotechAuthenticationProviderTest {

    @Mock
    private AllMotechUsers allMotechUsers;
    @Mock
    private MotechPasswordEncoder passwordEncoder;
    @Mock
    private AllMotechRoles allMotechRoles;

    private MotechAuthenticationProvider authenticationProvider;

    @Before
    public void setup() {
        initMocks(this);
        authenticationProvider = new MotechAuthenticationProvider(allMotechUsers, passwordEncoder, allMotechRoles);
    }

    @Ignore
    @Test
    public void shouldRetrieveUserFromDatabase() {
        MotechUser motechUser = new MotechUserCouchdbImpl("bob", "encodedPassword", "entity_1", "", asList("some_role"));
        when(allMotechUsers.findByUserName("bob")).thenReturn(motechUser);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("bob", "password");
        UserDetails userDetails = authenticationProvider.retrieveUser("bob", authentication);

        assertEquals("encodedPassword", userDetails.getPassword());
        assertEquals(motechUser.getUserName(), ((MotechUserProfile) authentication.getDetails()).getUserName());
        assertEquals(motechUser.getUserName(), userDetails.getUsername());
    }

    @Ignore
    @Test(expected = AuthenticationException.class)
    public void shouldThrowExceptionIfUserDoesntExist() {
        when(allMotechUsers.findByUserName("bob")).thenReturn(null);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("bob", "password");
        authenticationProvider.retrieveUser("bob", authentication);
    }

    @Ignore
    @Test(expected = AuthenticationException.class)
    public void shouldThrowExceptionIfUserIsInactive() {
        MotechUserCouchdbImpl motechUser = new MotechUserCouchdbImpl("bob", "encodedPassword", "entity_1", "", asList("some_role"));
        motechUser.setActive(false);
        when(allMotechUsers.findByUserName("bob")).thenReturn(motechUser);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("bob", "password");
        authenticationProvider.retrieveUser("bob", authentication);
    }

    @Ignore
    @Test
    public void shouldAuthenticateUser() {
        when(passwordEncoder.isPasswordValid("encodedPassword", "password")).thenReturn(true);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("bob", "password");
        UserDetails user = mock(UserDetails.class);
        when(user.getPassword()).thenReturn("encodedPassword");

        authenticationProvider.additionalAuthenticationChecks(user, authentication);
    }

    @Ignore
    @Test(expected = AuthenticationException.class)
    public void shouldNotAuthenticateEmptyPassword() {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("bob", "");
        UserDetails user = mock(UserDetails.class);
        when(user.getPassword()).thenReturn("encodedPassword");

        authenticationProvider.additionalAuthenticationChecks(user, authentication);
    }

    @Ignore
    @Test(expected = AuthenticationException.class)
    public void shouldNotAuthenticateWrongPassword() {
        when(passwordEncoder.isPasswordValid("encodedPassword", "password")).thenReturn(false);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("bob", "");
        UserDetails user = mock(UserDetails.class);
        when(user.getPassword()).thenReturn("encodedPassword");

        authenticationProvider.additionalAuthenticationChecks(user, authentication);
    }
}

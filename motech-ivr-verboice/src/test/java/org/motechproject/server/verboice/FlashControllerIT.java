package org.motechproject.server.verboice;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/ivrVerboiceContext.xml"})
public class FlashControllerIT {

    @Value("#{verboiceProperties.port}")
    private String verboicePort;

    @Autowired
    FlashController flashController;

    private VerboiceCallHandlerStub verboiceCallHandlerStub;


    @Before
    public void before() throws Exception {
        verboiceCallHandlerStub = new VerboiceCallHandlerStub();
        verboiceCallHandlerStub.getHttpServer(Integer.valueOf(verboicePort)).start();
    }

    @Test
    public void shouldFlash() throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("from", "1234");
        flashController.flash(request);

        verboiceCallHandlerStub.waitForRequest();

        assertEquals("/api/call", verboiceCallHandlerStub.getRequestURI());
        assertEquals("a", verboiceCallHandlerStub.getChannel());
        assertEquals("1234", verboiceCallHandlerStub.getAddress());
    }
}



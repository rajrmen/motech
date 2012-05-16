package org.motechproject.server.verboice.it;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.server.verboice.VerboiceCallHandlerStub;
import org.motechproject.server.verboice.VerboiceIVRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/ivrVerboiceContext.xml"})
public class VerboiceIVRServiceIT {
    private static final String PHONE_NUMBER = "1234";
    private VerboiceCallHandlerStub verboiceCallHandlerStub;

    @Value("#{verboiceProperties['outgoing.channel']}")
    private String channel;

    @Value("#{verboiceProperties.port}")
    private String verboicePort;

    @Autowired
    private VerboiceIVRService verboiceIVRService;

    @Before
    public void setUp() throws Exception {
        this.verboiceCallHandlerStub = new VerboiceCallHandlerStub();
        verboiceCallHandlerStub.getHttpServer(Integer.valueOf(verboicePort)).start();
    }

    @Test
    public void shouldInitiateCall() throws Exception{
        CallRequest request = new CallRequest(PHONE_NUMBER,2000, channel);
        verboiceIVRService.initiateCall(request);
        assertEquals(PHONE_NUMBER, verboiceCallHandlerStub.getAddress());
    }
}

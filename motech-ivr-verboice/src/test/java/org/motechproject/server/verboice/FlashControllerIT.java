package org.motechproject.server.verboice;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/ivrVerboiceContext.xml"})
public class FlashControllerIT {

    @Autowired
    FlashController flashController;

    @Test
    @Ignore("run with softphone until verboice is stubbed")
    public void shouldFlash() throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("from", "1234");
        flashController.flash(request);
    }
}

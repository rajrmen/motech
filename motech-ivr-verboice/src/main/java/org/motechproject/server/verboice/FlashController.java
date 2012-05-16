package org.motechproject.server.verboice;

import org.motechproject.ivr.service.CallRequest;
import org.motechproject.ivr.service.IVRService;
import org.motechproject.server.verboice.domain.VerboiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Timer;
import java.util.TimerTask;

@Controller
@RequestMapping("/ivr")
public class FlashController {
    @Autowired
    IVRService ivrService;

    @Value("#{verboiceProperties['flash.waitTimeInMillis']}")
    private String flashWaitTime;
    @Value("#{verboiceProperties['outgoing.channel']}")
    String outgoingChannel;

    @RequestMapping("/flash")
    @ResponseBody
    public String flash(HttpServletRequest request) {
        final String from = request.getParameter("from");
        final CallRequest callRequest = new CallRequest(from, request.getParameterMap(), outgoingChannel);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                ivrService.initiateCall(callRequest);
            }
        }, Integer.valueOf(flashWaitTime));

        return new VerboiceResponse().hangup().toXMLString();
    }
}

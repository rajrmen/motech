package org.motechproject.server.verboice;

import org.mortbay.jetty.Server;
import org.motechproject.testing.utils.HttpServiceStub;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class VerboiceCallHandlerStub extends HttpServiceStub {

    private String channel;
    private String address;
    private String requestURI;

    @Override
    protected void handleGet(HttpServletRequest req, HttpServletResponse resp) {
        requestURI = req.getRequestURI();
        channel = req.getParameter("channel");
        address = req.getParameter("address");
    }

    public String getChannel() {
        return channel;
    }

    public String getAddress() {
        return address;
    }

    public String getRequestURI() {
        return requestURI;
    }
}

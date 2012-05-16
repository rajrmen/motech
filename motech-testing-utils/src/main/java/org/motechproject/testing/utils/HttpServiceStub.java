package org.motechproject.testing.utils;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static junit.framework.Assert.fail;

public class HttpServiceStub extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            handleGet(req, resp);
        } finally {
           this.releaseLock();
        }
    }

    protected void handleGet(HttpServletRequest req, HttpServletResponse resp) {
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            handlePost(req, resp);
        } finally {
            this.releaseLock();
        }
    }

    protected void handlePost(HttpServletRequest req, HttpServletResponse resp) {
    }

    public Server getHttpServer(int port) {
        Server verboiceServer = new Server(port);
        Context context = new Context();
        context.setContextPath("/");

        ServletHolder servletHolder = new ServletHolder(this);
        context.addServlet(servletHolder, "/");
        verboiceServer.setHandler(context);
        return verboiceServer;
    }

    public void waitForRequest() throws InterruptedException {
        synchronized (this){ this.wait(); }
    }

    private void releaseLock(){
        synchronized (this){ this.notify(); }
    }
}

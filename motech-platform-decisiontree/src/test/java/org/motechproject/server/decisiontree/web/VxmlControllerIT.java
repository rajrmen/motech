package org.motechproject.server.decisiontree.web;

import org.ektorp.CouchDbConnector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.webapp.WebAppContext;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.File;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationDecisionTree.xml"})
public class VxmlControllerIT extends SpringIntegrationTest {


    @Test
    public void shouldReturnVXML() throws Exception {

        Server server = new Server(8080);
        WebAppContext context = new WebAppContext("tree", "/");//new Context(server, "/", Context.SESSIONS);

        context.setWar("motech-platform-decisiontree/src/main/resources");
        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        dispatcherServlet.setContextConfigLocation("classpath:applicationDecisionTree.xml");

        ServletHolder servletHolder = new ServletHolder(dispatcherServlet);
        context.addServlet(servletHolder, "/*");
        System.out.println( new File(".").getCanonicalPath());
        server.setHandler(context);
        server.start();
        server.join();
        Thread.sleep(1000000);
    }

    @Autowired
    @Qualifier("treesDatabase")
    private CouchDbConnector connector;

    @Override
    public CouchDbConnector getDBConnector() {
        return connector;
    }
}

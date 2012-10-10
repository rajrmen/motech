package org.motechproject.decisiontree.server.web;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.motechproject.decisiontree.core.model.AudioPrompt;
import org.motechproject.decisiontree.core.model.ITransition;
import org.motechproject.decisiontree.core.model.Node;
import org.motechproject.decisiontree.core.model.TextToSpeechPrompt;
import org.motechproject.decisiontree.core.model.Transition;
import org.motechproject.decisiontree.core.model.Tree;
import org.motechproject.decisiontree.core.repository.AllTrees;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.HashMap;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:META-INF/motech/*.xml")
public class DecisionTreeControllerTest extends SpringIntegrationTest {

    @Autowired
    private AllTrees allTrees;

    @Autowired
    @Qualifier("treesDatabase") private CouchDbConnector dbConnector;

    @Autowired
    private DecisionTreeController decisionTreeController;

    private static Server server;

    @After
    public void tearDown() {
        allTrees.removeAll();
    }

    @BeforeClass
    public static void startServer() throws Exception {
        server = new Server(7080);
        Context context = new Context(server, "/motech");

        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        dispatcherServlet.setContextConfigLocation("classpath*:META-INF/motech/*.xml");

        ServletHolder servletHolder = new ServletHolder(dispatcherServlet);
        context.addServlet(servletHolder, "/*");
        server.setHandler(context);
        server.start();
    }

    @AfterClass
    public static void stopServer() throws Exception {
        server.stop();
    }

    @Test
    public void shouldPersistGivenTree() throws Exception {
        HttpClient httpClient = new HttpClient();
        PostMethod method = new PostMethod("http://localhost:7080/motech/trees/create");
        String tree = createJsonTree();

        method.addRequestHeader("Content-Type", "application/json");

        method.setRequestEntity(new StringRequestEntity(tree.toString(), "application/json", "UTF-8"));
        httpClient.executeMethod(method);

        assertNotNull(allTrees.findByName("testTree"));
    }

    @Test
    public void shouldReturnTreeJsonByName() throws Exception {
        Tree tree = new Tree();
        HashMap<String, ITransition> transitions = new HashMap<String, ITransition>();
        final Node textToSpeechNode = new Node().addPrompts(new TextToSpeechPrompt().setName("Say this")).setMaxTransitionTimeout(25);
        transitions.put("1", new Transition().setDestinationNode(textToSpeechNode));
        final Node audioPromptNode = new Node().addPrompts(new AudioPrompt().setName("abc")).setTransitions(transitions);
        tree.setName("treeName").setRootTransition(new Transition().setDestinationNode(audioPromptNode));

        allTrees.addOrReplace(tree);
        markForDeletion(tree);

        HttpClient httpClient = new HttpClient();
        HttpMethod method = new GetMethod("http://localhost:7080/motech/trees/" + tree.getId());
        httpClient.executeMethod(method);

        System.out.println(method.getResponseBodyAsString());
    }

    private String createJsonTree() {
        String treeJson = "{\n" +
                "  \"name\": \"testTree\", \n" +
                "  \"type\": \"Tree\", \n" +
                "  \"description\": \"Some Description\",\n" +
                "  \"rootTransition\": {\n" +
                "    \"@type\": \"org.motechproject.decisiontree.core.model.Transition\",\n" +
                "    \"destinationNode\": {\n" +
                "      \"@type\": \"org.motechproject.decisiontree.core.model.Node\",\n" +
                "      \"noticePrompts\": [{\"@type\": \"org.motechproject.decisiontree.core.model.TextToSpeechPrompt\", \"message\": \"Welcome to motech\"}]\n" +
                "    }\n" +
                "  }\n" +
                "}";

        return treeJson;
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return dbConnector;
    }
}

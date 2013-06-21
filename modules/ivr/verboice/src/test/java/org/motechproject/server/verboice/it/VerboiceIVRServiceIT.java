package org.motechproject.server.verboice.it;

import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.callflow.service.FlowSessionService;
import org.motechproject.decisiontree.core.DecisionTreeService;
import org.motechproject.decisiontree.core.FlowSession;
import org.motechproject.decisiontree.core.model.Node;
import org.motechproject.decisiontree.core.model.TextToSpeechPrompt;
import org.motechproject.decisiontree.core.model.Transition;
import org.motechproject.decisiontree.core.model.Tree;
import org.motechproject.ivr.repository.AllCallDetailRecords;
import org.motechproject.ivr.service.contract.CallRequest;
import org.motechproject.server.verboice.VerboiceIVRService;
import org.motechproject.server.verboice.web.VerboiceIVRController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/motech/*.xml"})
public class VerboiceIVRServiceIT extends VerboiceTest {

    //    private static final String CALL_SID = "10001010";
    @Autowired
    private VerboiceIVRService verboiceIVRService;

    @Autowired
    VerboiceIVRController verboiceIVRController;

    @Autowired
    FlowSessionService flowSessionService;

//    @Autowired
//    AllFlowSessionRecords allFlowSessionRecords;

    @Autowired
    AllCallDetailRecords allCallRecords;

    @Autowired
    DecisionTreeService decisionTreeService;


    Tree tree;

    @Before
    public void setup() {

        tree = new Tree()
                .setName("Verboice_IVR_test_tree")
                .setRootTransition(new Transition().setDestinationNode(new Node()
                        .setPrompts(new TextToSpeechPrompt().setMessage("Press 1"))
                        .setTransitions(new Object[][]{
                                {"1", new Transition().setName("pressed1")
                                        .setDestinationNode(new Node()
                                                .setPrompts(new TextToSpeechPrompt().setMessage("You pressed 1 Thank you."))
                                        )}
                        })
                ));

        decisionTreeService.saveDecisionTree(tree);
    }


    @Test
//    @Ignore("run with verboice")
    public void shouldInitiateCallAndCreateACallDetailRecord() {
//        CallRequest callRequest = new CallRequest("16317099039",2000,"http://3r4x.localtunnel.com/motech-platform-server/module/verboice/web-api/ivr");
        CallRequest callRequest = new CallRequest("16317099127", 2000, null);

        HashMap<String, String> params = new HashMap<String, String>();
//        params.put(VerboiceIVRService.CALLBACK_URL,"http://motech:motech@5992.localtunnel.com/motech-platform-server/module/verboice/web-api/ivr");
        params.put(VerboiceIVRService.CALLBACK_URL, "http://motech:motech@3hv5.localtunnel.com/motech-platform-server/module/verboice/web-api/ivr?tree=Verboice_IVR_test_tree&ln=en");


        callRequest.setPayload(params);

        flowSessionService.findOrCreate(callRequest.getCallId(), callRequest.getPhone());

        FlowSession session = flowSessionService.getSession(callRequest.getCallId());

        Assert.assertNotNull(session);

        verboiceIVRService.initiateCall(callRequest);

        //call id -get it


        FlowSession newsession = flowSessionService.getSession(callRequest.getCallId());

        Assert.assertNull(newsession);


        //create a listener for IvrEvent of subject Dtmf("ivr.dtmf") //as our test tree requires user to press 1
        //the listner is notify the current thread when the event is received


        //wait long time or until it is notified by the listener


        // verify from the event that 1 is pressed


        //TODO: verify call detail record has been created.
    }


    @Override
    public CouchDbConnector getDBConnector() {
        return null;
    }

    @After
    public void tearDown(){
        decisionTreeService.deleteDecisionTree(tree.getId());
    }

}

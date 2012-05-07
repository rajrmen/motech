package org.motechproject.decisiontree.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.decisiontree.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testDecisionTreeCommon.xml"})
public class AllTreesTest {

    @Autowired
    AllTrees allTrees;

    @Test
    public void shouldStoreTree() throws Exception {
        Tree tree = new Tree();
        HashMap<String, Transition> transitions = new HashMap<String, Transition>();
        final Node textToSpeechNode = new Node().addPrompts(new TextToSpeechPrompt().setName("Say this"));
        transitions.put("1", new Transition().setDestinationNode(textToSpeechNode));
        final Node audioPromptNode = new Node().addPrompts(new AudioPrompt().setName("abc")).setTransitions(transitions);
        tree.setRootNode(audioPromptNode);

        allTrees.add(tree);

        Tree fromDb = allTrees.get(tree.getId());
        assertNotNull(fromDb);
        final Node rootNodeFromDb = fromDb.getRootNode();
        assertNotNull(rootNodeFromDb);
        assertEquals(AudioPrompt.class.getName(), rootNodeFromDb.getPrompts().get(0).getClass().getName());

        final Node nextNode = rootNodeFromDb.getTransitions().get("1").getDestinationNode();
        assertEquals(TextToSpeechPrompt.class.getName(), nextNode.getPrompts().get(0).getClass().getName());

    }

    @Test
    public void shouldStoreTreeWithCommands() throws Exception {
        Tree tree = new Tree();
        tree.setRootNode(new Node().addPrompts(new AudioPrompt().setCommand(new TestCommand())));
        allTrees.add(tree);

        Tree fromDb = allTrees.get(tree.getId());
        assertNotNull(fromDb);
        final ITreeCommand command = fromDb.getRootNode().getPrompts().get(0).getCommand();
        assertEquals(TestCommand.class.getName(), command.getClass().getName());
        final String[] result = command.execute(null);
        assertEquals("ok", result[0]);

    }


}

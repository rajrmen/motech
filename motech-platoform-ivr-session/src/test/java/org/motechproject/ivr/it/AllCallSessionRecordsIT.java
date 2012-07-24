package org.motechproject.ivr.it;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ivr.domain.FlowSession;
import org.motechproject.ivr.domain.FlowSessionImpl;
import org.motechproject.ivr.repository.AllCallSessionRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationPlatformIVR.xml")
public class AllCallSessionRecordsIT {

    @Autowired
    private AllCallSessionRecords allCallSessionRecords;

    @Test
    public void shouldCreateACallSessionRecordInDb() {
        FlowSessionImpl flowSession = new FlowSessionImpl("session1");
        ArrayList<String> value = new ArrayList<String>();
        value.add("value1");
        value.add("value2");
        value.add("value3");
        flowSession.add("key", value);

        allCallSessionRecords.add(flowSession);

        List<FlowSessionImpl> flowSessions = allCallSessionRecords.getAll();
        assertThat(flowSessions.size(), is(1));

        FlowSessionImpl actual = flowSessions.get(0);
        assertThat(actual, is(flowSession));
        List<String> values = actual.<ArrayList<String>>valueFor("key");
        assertThat(values.size(), is(3));
        assertThat(values.get(0), is("value1"));
        assertThat(values.get(1), is("value2"));
        assertThat(values.get(2), is("value3"));
    }

    @Test
    public void shouldFindOrCreateACallSessionRecord() {
        assertThat(allCallSessionRecords.getAll().size(), is(0));
        FlowSession aNew = allCallSessionRecords.findOrCreate("session1");
        assertThat(allCallSessionRecords.getAll().size(), is(1));

        FlowSession existing = allCallSessionRecords.findOrCreate("session1");
        assertThat(allCallSessionRecords.getAll().size(), is(1));
        assertThat(existing, is(equalTo(aNew)));
    }

    @Test
    public void shouldIgnoreCaseWhileFindOrCreateACallSessionRecord() {
        assertThat(allCallSessionRecords.getAll().size(), is(0));
        allCallSessionRecords.findOrCreate("Session1");
        assertThat(allCallSessionRecords.getAll().size(), is(1));

        allCallSessionRecords.findOrCreate("SESSION1");
        assertThat(allCallSessionRecords.getAll().size(), is(1));
    }

    @Test
    public void shouldFindBySessionId() {
        FlowSessionImpl flowSession1 = new FlowSessionImpl("S1");
        FlowSessionImpl flowSession2 = new FlowSessionImpl("S2");

        allCallSessionRecords.add(flowSession1);
        allCallSessionRecords.add(flowSession2);

        FlowSessionImpl actual = allCallSessionRecords.findBySessionId("s1");

        assertNotNull(actual);
        assertThat(actual, is(flowSession1));

        FlowSession nonExistent = allCallSessionRecords.findBySessionId("s3");
        assertNull(nonExistent);
    }

    @Test
    public void shouldIgnoreCaseWhileFindingBySessionId() {
        FlowSessionImpl flowSession = new FlowSessionImpl("IGNORE-CASE");

        allCallSessionRecords.add(flowSession);

        FlowSessionImpl actual = allCallSessionRecords.findBySessionId("ignore-CASE");

        assertNotNull(actual);
        assertThat(actual, is(flowSession));
    }

    @Test
    public void shouldUpdateTheExistingRecord() {
        FlowSessionImpl flowSession1 = new FlowSessionImpl("S1");

        allCallSessionRecords.add(flowSession1);
        FlowSessionImpl actual = allCallSessionRecords.findBySessionId("s1");
        assertNull(actual.valueFor("k1"));

        actual.add("k1", "v1");
        allCallSessionRecords.update(actual);

        FlowSessionImpl updated = allCallSessionRecords.findBySessionId("s1");
        assertNotNull(updated.valueFor("k1"));
    }

    @After
    public void tearDown() {
        allCallSessionRecords.removeAll();
    }
}

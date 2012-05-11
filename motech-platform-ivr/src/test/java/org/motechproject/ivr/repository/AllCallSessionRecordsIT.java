package org.motechproject.ivr.repository;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ivr.domain.CallSessionRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

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
        CallSessionRecord callSessionRecord = new CallSessionRecord("session1");
        callSessionRecord.add("key", "value");

        allCallSessionRecords.add(callSessionRecord);

        List<CallSessionRecord> callSessionRecords = allCallSessionRecords.getAll();
        assertThat(callSessionRecords.size(), is(1));

        CallSessionRecord actualRecord = callSessionRecords.get(0);
        assertThat(actualRecord, is(callSessionRecord));
        assertThat(actualRecord.valueFor("key"), is("value"));
    }

    @Test
    public void shouldFindBySessionId() {
        CallSessionRecord callSessionRecord1 = new CallSessionRecord("s1");
        CallSessionRecord callSessionRecord2 = new CallSessionRecord("s2");

        allCallSessionRecords.add(callSessionRecord1);
        allCallSessionRecords.add(callSessionRecord2);

        CallSessionRecord actualRecord = allCallSessionRecords.findBySessionId("s1");

        assertNotNull(actualRecord);
        assertThat(actualRecord, is(callSessionRecord1));

        CallSessionRecord nonExistentRecord = allCallSessionRecords.findBySessionId("s3");
        assertNull(nonExistentRecord);
    }

    @Test
    public void shouldUpdateTheExistingRecord() {
        CallSessionRecord callSessionRecord1 = new CallSessionRecord("s1");

        allCallSessionRecords.add(callSessionRecord1);
        CallSessionRecord actualRecord = allCallSessionRecords.findBySessionId("s1");
        assertNull(actualRecord.valueFor("k1"));

        actualRecord.add("k1", "v1");
        allCallSessionRecords.update(actualRecord);

        CallSessionRecord updatedRecord = allCallSessionRecords.findBySessionId("s1");
        assertNotNull(updatedRecord.valueFor("k1"));

    }
    @After
    public void tearDown() {
        allCallSessionRecords.removeAll();
    }
}

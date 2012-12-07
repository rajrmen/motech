package org.motechproject.ivr.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ivr.event.CallEvent;
import org.motechproject.ivr.model.CallDetailRecord;
import org.motechproject.ivr.repository.AllCallDetailRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/motech/*.xml")
public class CallDetailServiceImplTest {

    @Autowired
    private CallDetailServiceImpl callDetailService;
    @Autowired
    private AllCallDetailRecords allCallDetailRecords;

    @Before
    public void setUp() throws Exception {
        allCallDetailRecords.removeAll();
    }

    @Test
    public void shouldPersistCallDetail_WhenNotPresent() throws Exception {
        CallDetailRecord record = new CallDetailRecord(CallDetailRecord.Disposition.ANSWERED, "some error");
        assertTrue(allCallDetailRecords.getAll().isEmpty());

        CallDetailRecord callDetailRecord = callDetailService.createOrUpdate(record);

        assertNotNull(callDetailRecord.getId());

        CallDetailRecord recordInDB = allCallDetailRecords.get(callDetailRecord.getId());
        assertNotNull(recordInDB);
        assertEquals(CallDetailRecord.Disposition.ANSWERED, recordInDB.getDisposition());
        assertEquals("some error", recordInDB.getMessage());
    }

    @Test
    public void shouldUpdateCallDetails_WhenAlreadyExists() throws Exception {
        CallDetailRecord record = new CallDetailRecord(CallDetailRecord.Disposition.ANSWERED, "some error");
        allCallDetailRecords.add(record);

        CallDetailRecord updatedRecord = new CallDetailRecord(CallDetailRecord.Disposition.FAILED, "some other error");
        updatedRecord.setId(record.getId());
        updatedRecord.setRevision(record.getRevision());

        callDetailService.createOrUpdate(updatedRecord);

        CallDetailRecord recordInDB = allCallDetailRecords.get(record.getId());
        assertNotNull(recordInDB);
        assertEquals(CallDetailRecord.Disposition.FAILED, recordInDB.getDisposition());
        assertEquals("some other error", recordInDB.getMessage());
    }

    @Test
    public void shouldReturnCallDetailRecord_IfExists() throws Exception {
        CallDetailRecord record = new CallDetailRecord(CallDetailRecord.Disposition.ANSWERED, "some error");
        allCallDetailRecords.add(record);

        CallDetailRecord recordInDb = callDetailService.find(record.getId());

        assertNotNull(recordInDb);
    }


    @Test
    public void shouldAddEventsToGivenCallDetailRecord() throws Exception {
        CallDetailRecord record = new CallDetailRecord(CallDetailRecord.Disposition.ANSWERED, "some error");
        allCallDetailRecords.add(record);

        callDetailService.addCallEvent(record.getId(), new CallEvent("Answered"));
        callDetailService.addCallEvent(record.getId(), new CallEvent("End"));

        CallDetailRecord recordInDb = callDetailService.find(record.getId());
        assertEquals(2, recordInDb.getCallEvents().size());
        assertEquals("Answered", recordInDb.getCallEvents().get(0).getName());
        assertEquals("End", recordInDb.getCallEvents().get(1).getName());
    }
}

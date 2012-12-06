package org.motechproject.ivr.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ivr.model.CallDetailRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/motech/*.xml")
public class AllCallDetailRecordsTest {

    @Autowired
    private AllCallDetailRecords allCallDetailRecords;

    @Before
    public void setUp() throws Exception {
        allCallDetailRecords.removeAll();
    }

    @Test
    public void shouldPersistCallRecordData() throws Exception {
        CallDetailRecord record = new CallDetailRecord(CallDetailRecord.Disposition.ANSWERED, "failed");
        allCallDetailRecords.add(record);

        assertNotNull(record.getId());

        CallDetailRecord recordInDb = allCallDetailRecords.get(record.getId());

        assertEquals(CallDetailRecord.Disposition.ANSWERED, record.getDisposition());
        assertEquals("failed", record.getMessage());
    }
}

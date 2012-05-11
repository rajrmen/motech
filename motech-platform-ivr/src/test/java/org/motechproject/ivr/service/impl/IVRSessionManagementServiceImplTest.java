package org.motechproject.ivr.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.domain.CallSessionRecord;
import org.motechproject.ivr.repository.AllCallSessionRecords;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class IVRSessionManagementServiceImplTest {

    @Mock
    private AllCallSessionRecords allCallSessionRecords;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldGetAnCallSessionRecordBasedOnSessionId() {
        IVRSessionManagementServiceImpl ivrSessionManagementService = new IVRSessionManagementServiceImpl(allCallSessionRecords);

        ivrSessionManagementService.getCallSession("session1");

        verify(allCallSessionRecords, times(1)).findOrCreate("session1");
    }

    @Test
    public void shouldUpdateAnExistingSessionRecord()  {
        IVRSessionManagementServiceImpl ivrSessionManagementService = new IVRSessionManagementServiceImpl(allCallSessionRecords);

        CallSessionRecord callSessionRecord = mock(CallSessionRecord.class);
        ivrSessionManagementService.updateCallSession(callSessionRecord);

        verify(allCallSessionRecords, times(1)).update(callSessionRecord);
    }

    @Test
    public void shouldRemoveAnExistingSessionRecordBasedOnSessionId()  {
        IVRSessionManagementServiceImpl ivrSessionManagementService = new IVRSessionManagementServiceImpl(allCallSessionRecords);
        CallSessionRecord callSessionRecord = new CallSessionRecord("session1");
        when(allCallSessionRecords.findBySessionId("session1")).thenReturn(callSessionRecord);

        ivrSessionManagementService.removeCallSession("session1");

        verify(allCallSessionRecords, times(1)).remove(callSessionRecord);
    }

    @Test
    public void shouldNotRemoveASessionIfNotFound()  {
        IVRSessionManagementServiceImpl ivrSessionManagementService = new IVRSessionManagementServiceImpl(allCallSessionRecords);
        when(allCallSessionRecords.findBySessionId("session1")).thenReturn(null);

        ivrSessionManagementService.removeCallSession("session1");

        verify(allCallSessionRecords, times(0)).remove(any(CallSessionRecord.class));
    }
}

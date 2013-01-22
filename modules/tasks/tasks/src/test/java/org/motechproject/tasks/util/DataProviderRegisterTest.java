package org.motechproject.tasks.util;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.commons.api.DataProviderLookup;
import org.motechproject.tasks.domain.DataProvider;
import org.motechproject.tasks.service.DataProviderService;

import java.io.IOException;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class DataProviderRegisterTest {

    @Mock
    DataProviderService dataProviderService;

    @Mock
    DataProviderLookup dataProviderLookup;

    DataProviderRegister dataProviderRegister;

    String body = new DataProvider().toString();

    @Before
    public void setup() throws Exception {
        initMocks(this);

        dataProviderRegister = new DataProviderRegister(dataProviderService);
    }

    @Test
    public void shouldRegisterProviderWhenDataProviderServiceIsAvailable() throws IOException {
        when(dataProviderLookup.toJSON()).thenReturn(body);

        dataProviderRegister.bind(dataProviderLookup, null);

        verify(dataProviderService).registerProvider(body);
    }

    @Test
    public void shouldNotRegisterProviderWhenGotOtherServices() throws IOException {
        when(dataProviderLookup.toJSON()).thenReturn(body);

        dataProviderRegister.bind(new Object(), null);

        verify(dataProviderService, never()).registerProvider(body);
    }
}

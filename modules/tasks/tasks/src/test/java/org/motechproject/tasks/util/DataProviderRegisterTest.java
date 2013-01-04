package org.motechproject.tasks.util;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tasks.service.ChannelService;
import org.motechproject.tasks.service.DataProviderService;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class DataProviderRegisterTest {

    @Mock
    InputStream inputStream;

    @Mock
    Resource resource;

    @Mock
    DataProviderService dataProviderService;

    DataProviderRegister dataProviderRegister;

    @Before
    public void setup() throws Exception {
        initMocks(this);

        dataProviderRegister = new DataProviderRegister(resource);
    }

    @Test
    public void shouldRegisterProviderWhenDataProviderServiceIsAvailable() throws IOException {
        when(resource.getInputStream()).thenReturn(inputStream);

        dataProviderRegister.registered(dataProviderService, null);

        verify(dataProviderService).registerProvider(inputStream);
    }

    @Test
    public void shouldNotRegisterProviderWhenGotOtherServices() throws IOException {
        when(resource.getInputStream()).thenReturn(inputStream);

        dataProviderRegister.registered(new Object(), null);

        verify(dataProviderService, never()).registerProvider(inputStream);
    }
}

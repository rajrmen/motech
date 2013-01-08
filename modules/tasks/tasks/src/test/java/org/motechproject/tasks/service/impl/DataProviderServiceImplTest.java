package org.motechproject.tasks.service.impl;

import com.google.gson.reflect.TypeToken;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.tasks.domain.DataProvider;
import org.motechproject.tasks.repository.AllDataProviders;
import org.motechproject.tasks.service.DataProviderService;

import java.io.InputStream;
import java.lang.reflect.Type;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class DataProviderServiceImplTest {
    @Mock
    AllDataProviders allDataProviders;

    @Mock
    InputStream inputStream;

    @Mock
    MotechJsonReader motechJsonReader;

    DataProviderService dataProviderService;

    @Before
    public void setup() throws Exception {
        initMocks(this);

        dataProviderService = new DataProviderServiceImpl(allDataProviders, motechJsonReader);
    }

    @Test
    public void shouldRegisterProvider() {
        Type type = new TypeToken<DataProvider>() { }.getType();
        DataProvider provider = new DataProvider();

        when(motechJsonReader.readFromStream(inputStream, type)).thenReturn(provider);

        dataProviderService.registerProvider(inputStream);

        verify(motechJsonReader).readFromStream(inputStream, type);
        verify(allDataProviders).addOrUpdate(provider);
    }

}

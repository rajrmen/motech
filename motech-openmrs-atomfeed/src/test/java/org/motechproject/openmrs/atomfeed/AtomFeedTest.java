package org.motechproject.openmrs.atomfeed;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduler.gateway.OutboundEventGateway;
import org.springframework.core.io.ClassPathResource;

public class AtomFeedTest {
    
    @Mock
    OutboundEventGateway outboundGateway;
    
    @Mock
    OpenMrsHttpClient client;

    @Before
    public void setUp() {
        initMocks(this);
    }
    
    @Test
    public void shouldNotRaiseAnyEventsOnEmptyXml() throws IOException {
        AtomFeedClient atomFeedClient = new AtomFeedClient(client);
        
        when(client.getOpenMrsAtomFeed()).thenReturn("");
        atomFeedClient.fetchNewOpenMrsEvents();
        
        verify(outboundGateway, times(0)).sendEventMessage(any(MotechEvent.class));
    }
    
    @Test
    public void shouldRaisePatientCreateEvent() throws IOException {
        AtomFeedClient atomFeedClient = new AtomFeedClient(client);

        when(client.getOpenMrsAtomFeed()).thenReturn(readXmlFile("patient-create.xml"));
        
        atomFeedClient.fetchNewOpenMrsEvents();
        
        ArgumentCaptor<MotechEvent> event = ArgumentCaptor.forClass(MotechEvent.class);
        verify(outboundGateway, times(0)).sendEventMessage(event.capture());
        
        assertEquals(expectedMotechEventForCreatePatient(), event.getValue());
    }

    private Object expectedMotechEventForCreatePatient() {
        MotechEvent event = new MotechEvent("org.motechproject.openmrs.atomfeed.create.patient");
        Map<String, Object> params = event.getParameters();
        params.put("PATIENT_UUID", "uuid:64f6f0e2-1acc-4a00-8a54-6adcd8cbfdfc");
        params.put("AUTHOR", "Super User (admin)");
        params.put("ACTION", "create");
        params.put("LINK", "http://localhost:8092/openmrs/ws/rest/v1/person/64f6f0e2-1acc-4a00-8a54-6adcd8cbfdfc");
        return event;
    }

    private String readXmlFile(String string) throws IOException {
        ClassPathResource resource = new ClassPathResource(string);
        String read = IOUtils.toString(resource.getInputStream());
        resource.getInputStream().close();
        return read;
    }
}

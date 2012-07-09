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
import org.motechproject.openmrs.atomfeed.events.EventDataKeys;
import org.motechproject.openmrs.atomfeed.events.EventSubjects;
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

    private final static String PATIENT_LINK = "http://localhost:8092/openmrs/ws/rest/v1/person/64f6f0e2-1acc-4a00-8a54-6adcd8cbfdfc";
    private final static String PATIENT_UUID = "64f6f0e2-1acc-4a00-8a54-6adcd8cbfdfc";
    
    private static final String CONCEPT_UUID = "a8f54bd7-429a-43d1-b47a-5ee56f18f0f2";
    private static final String CONCEPT_LINK = "http://localhost:8092/openmrs/ws/rest/v1/concept/a8f54bd7-429a-43d1-b47a-5ee56f18f0f2";

    @Test
    public void shouldNotRaiseAnyEventsOnEmptyXml() throws IOException {
        AtomFeedClient atomFeedClient = new AtomFeedClient(client, outboundGateway);

        when(client.getOpenMrsAtomFeed()).thenReturn("");
        atomFeedClient.fetchNewOpenMrsEvents();

        verify(outboundGateway, times(0)).sendEventMessage(any(MotechEvent.class));
    }

    @Test
    public void shouldRaisePatientCreateEvent() throws IOException {
        AtomFeedClient atomFeedClient = new AtomFeedClient(client, outboundGateway);

        when(client.getOpenMrsAtomFeed()).thenReturn(readXmlFile("patient-create.xml"));

        atomFeedClient.fetchNewOpenMrsEvents();

        ArgumentCaptor<MotechEvent> event = ArgumentCaptor.forClass(MotechEvent.class);
        verify(outboundGateway, times(1)).sendEventMessage(event.capture());

        assertEquals(expectedMotechEventForPatient(EventSubjects.PATIENT_CREATE, "create", PATIENT_LINK, PATIENT_UUID), event.getValue());
    }

    private Object expectedMotechEventForPatient(String subject, String action, String link, String uuid) {
        MotechEvent event = new MotechEvent(subject);
        Map<String, Object> params = event.getParameters();
        params.put(EventDataKeys.UUID, uuid);
        params.put(EventDataKeys.AUTHOR, "Super User (admin)");
        params.put(EventDataKeys.ACTION, action);
        params.put(EventDataKeys.UPDATED, "2012-07-02T15:00:00-04:00");
        params.put(EventDataKeys.LINK, link);
        return event;
    }

    private String readXmlFile(String string) throws IOException {
        ClassPathResource resource = new ClassPathResource(string);
        String read = IOUtils.toString(resource.getInputStream());
        resource.getInputStream().close();
        return read;
    }

    @Test
    public void shouldRaisePatientUpdateEvent() throws IOException {
        AtomFeedClient atomFeedClient = new AtomFeedClient(client, outboundGateway);

        when(client.getOpenMrsAtomFeed()).thenReturn(readXmlFile("patient-update.xml"));

        atomFeedClient.fetchNewOpenMrsEvents();

        ArgumentCaptor<MotechEvent> event = ArgumentCaptor.forClass(MotechEvent.class);
        verify(outboundGateway, times(1)).sendEventMessage(event.capture());

        assertEquals(expectedMotechEventForPatient(EventSubjects.PATIENT_UPDATE, "update", PATIENT_LINK, PATIENT_UUID), event.getValue());
    }

    @Test
    public void shouldRaisePatientVoidEvent() throws IOException {
        AtomFeedClient atomFeedClient = new AtomFeedClient(client, outboundGateway);

        when(client.getOpenMrsAtomFeed()).thenReturn(readXmlFile("patient-void.xml"));

        atomFeedClient.fetchNewOpenMrsEvents();

        ArgumentCaptor<MotechEvent> event = ArgumentCaptor.forClass(MotechEvent.class);
        verify(outboundGateway, times(1)).sendEventMessage(event.capture());

        assertEquals(expectedMotechEventForPatient(EventSubjects.PATIENT_VOIDED, "void", PATIENT_LINK, PATIENT_UUID), event.getValue());
    }

    @Test
    public void shouldRaisePatientDeleteEvent() throws IOException {
        AtomFeedClient atomFeedClient = new AtomFeedClient(client, outboundGateway);

        when(client.getOpenMrsAtomFeed()).thenReturn(readXmlFile("patient-delete.xml"));

        atomFeedClient.fetchNewOpenMrsEvents();

        ArgumentCaptor<MotechEvent> event = ArgumentCaptor.forClass(MotechEvent.class);
        verify(outboundGateway, times(1)).sendEventMessage(event.capture());

        assertEquals(expectedMotechEventForPatient(EventSubjects.PATIENT_DELETED, "delete", PATIENT_LINK, PATIENT_UUID), event.getValue());
    }

    @Test
    public void shouldRaiseConceptEvent() throws IOException {
        AtomFeedClient atomFeedClient = new AtomFeedClient(client, outboundGateway);

        when(client.getOpenMrsAtomFeed()).thenReturn(readXmlFile("concept.xml"));

        atomFeedClient.fetchNewOpenMrsEvents();

        ArgumentCaptor<MotechEvent> event = ArgumentCaptor.forClass(MotechEvent.class);
        verify(outboundGateway, times(1)).sendEventMessage(event.capture());

        assertEquals(expectedMotechEventForPatient(EventSubjects.CONCEPT_CREATE, "create", CONCEPT_LINK, CONCEPT_UUID), event.getValue());
    }
}

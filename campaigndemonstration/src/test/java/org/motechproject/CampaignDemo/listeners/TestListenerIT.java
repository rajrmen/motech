package org.motechproject.CampaignDemo.listeners;



import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.CampaignDemo.dao.PatientDAO;
import org.motechproject.CampaignDemo.model.Patient;
import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.cmslite.api.repository.AllStringContents;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.ivr.service.IVRService;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;



@RunWith(MockitoJUnitRunner.class)
public class TestListenerIT {
	
	private TestListener listener;

	@Mock
	private AllMessageCampaigns campaigns;
	
	@Mock
	private AllStringContents stringContent;
	
	@Mock
	private PatientDAO patientDAO;
	
	@Mock
	private IVRService ivrService;
	
	@Mock
	private MessageCampaignService service;

	
    @Before
    public void initMocks() {
    	listener = new TestListener(campaigns, stringContent, patientDAO, ivrService, service);
     }
    
	@Test
	public void testWhenPatientExists() {

		CampaignMessage message = new CampaignMessage();
		message.messageKey("TestCampaignKey");
		
		MotechEvent event = new MotechEvent(EventKeys.MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT);
		event.getParameters().put(EventKeys.CAMPAIGN_NAME_KEY, "TestCampaign");
		event.getParameters().put(EventKeys.MESSAGE_KEY, "TestCampaignKey");
		event.getParameters().put(EventKeys.EXTERNAL_ID_KEY, "12345");
		
		List<Patient> patientList = new ArrayList<Patient>();
		Patient testPatient = new Patient("12345", "207");
		patientList.add(testPatient);
		
		
		Mockito.when(patientDAO.findByExternalid("12345")).thenReturn(patientList);
		Mockito.when(stringContent.getContent("en", "TestCampaignKey")).thenReturn(new StringContent("en", "cron-message", "demo.xml"));
		Mockito.when(campaigns.get("TestCampaign", "TestCampaignKey")).thenReturn(message);
		
		listener.execute(event);
		
		verify(ivrService).initiateCall(Matchers.any(CallRequest.class));

	}
	
	@Test
	public void testWhenPatientDoesNotExist() {
		CampaignMessage message = new CampaignMessage();
		message.messageKey("TestCampaignKey");
		
		MotechEvent event = new MotechEvent(EventKeys.MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT);
		event.getParameters().put(EventKeys.CAMPAIGN_NAME_KEY, "TestCampaign");
		event.getParameters().put(EventKeys.MESSAGE_KEY, "TestCampaignKey");
		event.getParameters().put(EventKeys.EXTERNAL_ID_KEY, "12345");
		
		Mockito.when(patientDAO.findByExternalid("12345")).thenReturn(new ArrayList<Patient>());
		Mockito.when(stringContent.getContent("en", "TestCampaignKey")).thenReturn(new StringContent("en", "cron-message", "demo.xml"));
		Mockito.when(campaigns.get("TestCampaign", "TestCampaignKey")).thenReturn(message);
		
		
		listener.execute(event);
		
		verify(service).stopAll(Matchers.any(CampaignRequest.class));
	}
	
}

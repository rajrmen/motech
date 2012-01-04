package org.motechproject.CampaignDemo.listeners;


import java.util.List;

import org.motechproject.CampaignDemo.dao.PatientDAO;
import org.motechproject.CampaignDemo.model.Patient;
import org.motechproject.cmslite.api.repository.AllStringContents;
import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.ivr.service.IVRService;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;
import org.springframework.beans.factory.annotation.Autowired;

public class TestListener {
	
	@Autowired
	private AllMessageCampaigns campaigns;
	
	@Autowired 
	private AllStringContents stringContent;
	
	@Autowired
	private PatientDAO patientDAO;
	
	@Autowired
	private IVRService ivrService;
	
	@Autowired
	private MessageCampaignService service;
	
	public TestListener(AllMessageCampaigns campaigns, AllStringContents stringContent, PatientDAO patientDAO,
			IVRService ivrService, MessageCampaignService service) {
		this.campaigns = campaigns;
		this.stringContent = stringContent;
		this.patientDAO = patientDAO;
		this.ivrService = ivrService;
		this.service = service;
	}
	
	@MotechListener(subjects={EventKeys.MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT})
	public void execute(MotechEvent event) {
		System.out.println("Executing campaign message...");
		
		String campaignName = (String) event.getParameters().get(EventKeys.CAMPAIGN_NAME_KEY);
		String messageKey = (String) event.getParameters().get(EventKeys.MESSAGE_KEY);
		String externalId = (String) event.getParameters().get(EventKeys.EXTERNAL_ID_KEY);
		
		CampaignMessage campaignMessage = campaigns.get(campaignName, messageKey);
		
		StringContent content = stringContent.getContent("en", campaignMessage.messageKey());
		
		List<Patient> patientList = patientDAO.findByExternalid(externalId);
		
		if (patientList.size() == 0) {
			System.out.println("No patient by that id, unable to handle. Unscheduling the campaign job.");
			CampaignRequest toRemove = new CampaignRequest();
			toRemove.setCampaignName(campaignName);
			toRemove.setExternalId(externalId);
			service.stopAll(toRemove);
			//service.stopFor(toRemove, messageKey);
			return;
		} else {
		
		Patient thePatient = patientList.get(0);

		String phoneNum = thePatient.getPhoneNum();
		String vxmlUrl = content.getValue();

		System.out.println("Calling: " + phoneNum);
		
		CallRequest request = new CallRequest(phoneNum, 45, vxmlUrl);
		
		ivrService.initiateCall(request);
		}
	}
}

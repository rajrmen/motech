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

/**
 * A listener class used to listen on fired campaign message events.
 * This class demonstrates how to listen in on events and taking action based
 * upon their payload. Payloads are stored as a String-Object mapping pair, where the String
 * is found in an appropriate EventKey class and the Object is the relevant data or information
 * associated with the key. The payload information should be known ahead of time.
 * 
 * AllMessageCampaigns accesses the simple-message-campaign.json file found
 * in the resource package in the demo. The json file defines the characteristics
 * of a campaign.
 * 
 * @author Russell Gillen
 *
 */
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
	
	/**
	 * Methods are registered as listeners on specific motech events. All motech events
	 * have an associated subject, which is found in an appropriate EventKeys class.
	 * When an event with that particular subject is relayed, this method will be invoked.
	 * The payload parameters, in this case, campaign name, message key and external id, must be known
	 * ahead of time.
	 *
	 * @param event The Motech event relayed by the EventRelay
	 */
	@MotechListener(subjects={EventKeys.MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT})
	public void execute(MotechEvent event) {
		
		String campaignName = (String) event.getParameters().get(EventKeys.CAMPAIGN_NAME_KEY);
		String messageKey = (String) event.getParameters().get(EventKeys.MESSAGE_KEY);
		String externalId = (String) event.getParameters().get(EventKeys.EXTERNAL_ID_KEY);
		
		CampaignMessage campaignMessage = campaigns.get(campaignName, messageKey);
		
		StringContent content = stringContent.getContent("en", campaignMessage.messageKey());
		
		List<Patient> patientList = patientDAO.findByExternalid(externalId);
		
		if (patientList.size() == 0) {
			//In the event no patient was found, the campaign is unscheduled
			CampaignRequest toRemove = new CampaignRequest();
			toRemove.setCampaignName(campaignName);
			toRemove.setExternalId(externalId);
			service.stopAll(toRemove);
			//This will stop the specific message: service.stopFor(toRemove, messageKey);
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

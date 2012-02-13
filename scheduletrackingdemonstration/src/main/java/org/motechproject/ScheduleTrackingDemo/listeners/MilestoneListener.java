package org.motechproject.ScheduleTrackingDemo.listeners;

import java.util.List;
import java.util.Map;

import org.motechproject.ScheduleTracking.model.Patient;
import org.motechproject.ScheduleTrackingDemo.OpenMrsClient;
import org.motechproject.ScheduleTrackingDemo.DAO.MRSPatientDAO;
import org.motechproject.cmslite.api.model.ContentNotFoundException;
import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.cmslite.api.service.CMSLiteService;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.ivr.service.IVRService;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.scheduletracking.api.events.constants.EventSubject;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.sms.api.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class MilestoneListener {

	private static Logger logger = LoggerFactory
			.getLogger(MilestoneListener.class);

	private static final String SMS_FORMAT = "SMS";
	private static final String IVR_FORMAT = "IVR";

	@Autowired
	private IVRService voxeoService;

	@Autowired
	private OpenMrsClient openmrsClient;

	@Autowired
	private ScheduleTrackingService scheduleTrackingService;

	@Autowired
	private MRSPatientDAO patientDAO;

	@Autowired
	private CMSLiteService cmsliteService;
	
	@Autowired
	private SmsService smsService;

	@MotechListener(subjects = { EventSubject.MILESTONE_ALERT })
	public void execute(MotechEvent event) {
		logger.debug("Handled milestone event");
		MilestoneEvent mEvent = new MilestoneEvent(event);
		String milestoneConceptName = event.getParameters().get("conceptName")
				.toString();
		boolean hasFulfilledMilestone = openmrsClient.hasConcept(
				mEvent.getExternalId(), milestoneConceptName);

		if (hasFulfilledMilestone) {
			logger.debug("Fulfilling milestone for: " + mEvent.getExternalId()
					+ " with schedule: " + mEvent.getScheduleName());
			scheduleTrackingService.fulfillCurrentMilestone(
					mEvent.getExternalId(), mEvent.getScheduleName());
		} else { //Place calls and/or text messages
			
			List<Patient> patientList = patientDAO.findByExternalid(mEvent.getExternalId());
			
			if (patientList.size() > 0) {
				
				String IVRFormat = event.getParameters().get("IVRFormat").toString();
				String SMSFormat = event.getParameters().get("SMSFormat").toString();
				String language = event.getParameters().get("language").toString();
				String messageName = event.getParameters().get("messageName").toString();

				if ("true".equals(IVRFormat) && language != null && messageName != null) {
					this.placeCall(patientList.get(0), language, messageName);
				}
				if ("true".equals(SMSFormat) && language != null && messageName != null) {
					this.sendSMS(patientList.get(0), language, messageName);
				}
			}
		}
	}

	@MotechListener(subjects = { EventSubject.DEFAULTMENT_CAPTURE })
	public void defaulted(MotechEvent event) {
		System.out.println("Handled defaultment event");
	}

	private void placeCall(Patient patient, String language, String messageName) {
		if (cmsliteService.isStringContentAvailable(language, messageName, IVR_FORMAT)) {
			StringContent content = null;
			try {
				content = cmsliteService.getStringContent(language, messageName, IVR_FORMAT);
			} catch (ContentNotFoundException e) {
				e.printStackTrace();
			}
			if (content != null) {
				CallRequest request = new CallRequest(patient.getPhoneNum(), 119, content.getValue());
				voxeoService.initiateCall(request);
			}
		} else {
			logger.error("No IVR content available");
		}
	}

	private void sendSMS(Patient patient, String language, String messageName) {
		if (cmsliteService.isStringContentAvailable(language, messageName, SMS_FORMAT)) {
			StringContent content = null;
			try {
				content = cmsliteService.getStringContent(language, messageName, SMS_FORMAT);
			} catch (ContentNotFoundException e) {
			}
			smsService.sendSMS(patient.getPhoneNum(), content.getValue());
		} else { //no content, don't send SMS
			logger.error("No SMS content available");
		}

	}

}

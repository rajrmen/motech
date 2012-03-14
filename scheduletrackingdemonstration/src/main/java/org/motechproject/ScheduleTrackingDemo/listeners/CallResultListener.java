package org.motechproject.ScheduleTrackingDemo.listeners;


import org.motechproject.ivr.event.IVREventDelegate;
import org.motechproject.ivr.model.CallDetailRecord;
import org.motechproject.model.MotechEvent;
import org.motechproject.outbox.api.VoiceOutboxService;
import org.motechproject.outbox.api.model.MessagePriority;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.outbox.api.model.VoiceMessageType;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.voxeo.web.IvrController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class CallResultListener {

	private static Logger logger = LoggerFactory
			.getLogger(CallResultListener.class);


	@Autowired
	private VoiceOutboxService outboxService;

	//For demo purposes, successful calls are also stored in the outbox
	@MotechListener(subjects = {"CALL_BUSY" , "CALL_FAIL", "CALL_NO_ANSWER", "CALL_SUCCESS"})
	public void execute(MotechEvent event) {
		System.out.println("Handling event: " + event.getSubject());
		logger.debug("Handled call event");

		//if (!event.getSubject().equals("CALL_SUCCESS")) { //Call was not successful, store in outbox

			String externalId = (String) event.getParameters().get(IvrController.CALL_RESULT_ID);
			
			CallDetailRecord cdr = (CallDetailRecord) event.getParameters().get(IVREventDelegate.CALL_DETAIL_RECORD_KEY);


			OutboundVoiceMessage outboundVoiceMessage = new OutboundVoiceMessage();
			outboundVoiceMessage.setPartyId(cdr.getPhoneNumber()); //Content is identified by phone number for now


			VoiceMessageType voiceMessageType = new VoiceMessageType();
			voiceMessageType.setCanBeReplayed(true);
			voiceMessageType.setCanBeSaved(true);
			voiceMessageType.setPriority(MessagePriority.MEDIUM);
			
			//Template work-around for now, template name is based on the content
			
			String content = (String) event.getParameters().get(IvrController.CALL_CONTENT);
			String template = content.replace(".xml", "");
			voiceMessageType.setTemplateName(template);


			outboundVoiceMessage.setVoiceMessageType(voiceMessageType);

			outboxService.addMessage(outboundVoiceMessage);

		}
	}



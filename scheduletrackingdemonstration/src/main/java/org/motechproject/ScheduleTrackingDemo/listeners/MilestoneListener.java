package org.motechproject.ScheduleTrackingDemo.listeners;

import org.motechproject.ScheduleTrackingDemo.OpenMrsClient;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.scheduletracking.api.events.constants.EventSubject;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class MilestoneListener {

	private static Logger logger = LoggerFactory
			.getLogger(MilestoneListener.class);

	@Autowired
	private OpenMrsClient openmrsClient;

	@Autowired
	private ScheduleTrackingService scheduleTrackingService;

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
		}
	}

	@MotechListener(subjects = { EventSubject.DEFAULTMENT_CAPTURE })
	public void defaulted(MotechEvent event) {
		System.out.println("Handled defaultment event");
	}
}

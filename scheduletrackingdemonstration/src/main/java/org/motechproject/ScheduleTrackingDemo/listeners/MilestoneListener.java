package org.motechproject.ScheduleTrackingDemo.listeners;


import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.scheduletracking.api.events.constants.EventSubject;


public class MilestoneListener {


	@MotechListener(subjects={EventSubject.MILESTONE_ALERT})
	public void execute(MotechEvent event) {
		 System.out.println("Handled milestone event");
		 Map<String, Object> map = event.getParameters();
		 for (Entry<String, Object> entry : map.entrySet())
		 {
		     System.out.println(entry.getKey().toString() + "/" + entry.getValue().toString());
		 }
	}
	 
	@MotechListener(subjects={EventSubject.DEFAULTMENT_CAPTURE})
	public void defaulted(MotechEvent event) {
		System.out.println("Handled defaultment event");
	}
}


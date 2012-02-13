package org.motechproject.ScheduleTrackingDemo.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.motechproject.ScheduleTrackingDemo.OpenMrsClient;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.Milestone;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.repository.AllTrackedSchedules;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;


public class EnrollController extends MultiActionController {

	@Autowired
	private ScheduleTrackingService scheduleTrackingService; 
	
	@Autowired
	private AllTrackedSchedules allSchedules;
	
	@Autowired
	AllEnrollments allEnrollments;
	
	@Autowired
	private OpenMrsClient openMrsClient;
	

	public ModelAndView start(HttpServletRequest request, HttpServletResponse response) {
			
		String externalID = request.getParameter("externalID");
		String scheduleName = request.getParameter("scheduleName");
		
		Schedule schedule = allSchedules.getByName(scheduleName);
		
		if (schedule == null) System.out.println("Schedule null");
		
		String lastConceptFulfilled = "";
		String checkConcept;
		
		for (Milestone milestone : schedule.getMilestones()) {
			checkConcept = milestone.getData().get("conceptName");
			if (checkConcept != null) {
				if (openMrsClient.hasConcept(externalID, checkConcept)) {
					System.out.println(lastConceptFulfilled);
					lastConceptFulfilled = checkConcept;
					System.out.println(lastConceptFulfilled);
				}
			}
		}
		
		EnrollmentRequest enrollmentRequest;
		
		if (lastConceptFulfilled.equals("")) { //enroll in new schedule
			 enrollmentRequest = new EnrollmentRequest(externalID, scheduleName, null, DateUtil.now());
		} else { //start at the next milestone
			Enrollment enrollment = allEnrollments.findActiveByExternalIdAndScheduleName(externalID, scheduleName);
			if (enrollment == null) {
				enrollmentRequest = new EnrollmentRequest(externalID, scheduleName, null, DateUtil.now(), schedule.getNextMilestoneName(lastConceptFulfilled));
			} else { //Enrollment already exists, but now re-enrolling to whatever their latest last milestone fulfillment was, based on OpenMRS
				scheduleTrackingService.unenroll(externalID, scheduleName);
				enrollmentRequest = new EnrollmentRequest(externalID, scheduleName, null, DateUtil.now(), schedule.getNextMilestoneName(lastConceptFulfilled));
			}
		}
		
		scheduleTrackingService.enroll(enrollmentRequest);
		
		return new ModelAndView("scheduleTrackingPage");

	}
	
	/**
	 * For testing purposes only
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView fulfill(HttpServletRequest request, HttpServletResponse response) {

		String externalID = request.getParameter("externalID");
		String scheduleName = request.getParameter("scheduleName");
		
		EnrollmentRequest enrollmentRequest = new EnrollmentRequest(externalID, scheduleName, null, DateUtil.now());
		
		scheduleTrackingService.fulfillCurrentMilestone(externalID, scheduleName);
		
		return new ModelAndView("scheduleTrackingPage");

	}
}

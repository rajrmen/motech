package org.motechproject.ScheduleTrackingDemo.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;


public class EnrollController extends MultiActionController {

	@Autowired
	private ScheduleTrackingService scheduleTrackingService; 
	
	public ModelAndView start(HttpServletRequest request, HttpServletResponse response) {

		EnrollmentRequest enrollmentRequest = new EnrollmentRequest("RG-1", "IPTI Schedule", null, DateUtil.now());
		
		scheduleTrackingService.enroll(enrollmentRequest);
		
		return new ModelAndView("scheduleTrackingPage");

	}
	
	public ModelAndView fulfill(HttpServletRequest request, HttpServletResponse response) {

		EnrollmentRequest enrollmentRequest = new EnrollmentRequest("Russell Gillen", "IPTI Schedule", null, DateUtil.now());
		
		scheduleTrackingService.fulfillCurrentMilestone("Russell Gillen", "IPTI Schedule");
		
		return new ModelAndView("scheduleTrackingPage");

	}
}

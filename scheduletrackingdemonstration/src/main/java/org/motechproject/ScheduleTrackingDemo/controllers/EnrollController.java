package org.motechproject.ScheduleTrackingDemo.controllers;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.motechproject.ScheduleTracking.model.Patient;
import org.motechproject.ScheduleTrackingDemo.OpenMrsClient;
import org.motechproject.ScheduleTrackingDemo.PatientScheduler;
import org.motechproject.ScheduleTrackingDemo.DAO.MRSPatientDAO;
import org.motechproject.scheduletracking.api.domain.InvalidEnrollmentException;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;


public class EnrollController extends MultiActionController {
	
	@Autowired
	MRSPatientDAO patientDAO;

	@Autowired
	private ScheduleTrackingService scheduleTrackingService; 

	@Autowired
	AllEnrollments allEnrollments;

	@Autowired
	private OpenMrsClient openMrsClient;

	@Autowired
	private PatientScheduler patientSchedule;

	public ModelAndView start(HttpServletRequest request, HttpServletResponse response) {

		String externalID = request.getParameter("externalID");
		String scheduleName = request.getParameter("scheduleName");

		patientSchedule.enrollIntoSchedule(externalID, scheduleName);
		
		List<Patient> patientList = patientDAO.findAllPatients();
		
		Map<String, Object> modelMap = new TreeMap<String, Object>();
		modelMap.put("patients", patientList); //List of patients is for display purposes only
		
		ModelAndView mv = new ModelAndView("scheduleTrackingPage", modelMap);

		return mv;

	}

	public ModelAndView stop(HttpServletRequest request, HttpServletResponse response) {

		String externalID = request.getParameter("externalID");
		String scheduleName = request.getParameter("scheduleName");

		try {
			scheduleTrackingService.unenroll(externalID, scheduleName);
		} catch (InvalidEnrollmentException e) {
			logger.warn("Could not unenroll externalId=" + externalID + ", scheduleName=" + scheduleName);
		}

		List<Patient> patientList = patientDAO.findAllPatients();
		
		Map<String, Object> modelMap = new TreeMap<String, Object>();
		modelMap.put("patients", patientList); //List of patients is for display purposes only
		
		ModelAndView mv = new ModelAndView("scheduleTrackingPage", modelMap);

		return mv;
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

		List<Patient> patientList = patientDAO.findAllPatients();
		
		Map<String, Object> modelMap = new TreeMap<String, Object>();
		modelMap.put("patients", patientList); //List of patients is for display purposes only
		
		ModelAndView mv = new ModelAndView("scheduleTrackingPage", modelMap);

		return mv;

	}

	/**
	 * For testing purposes
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView obs(HttpServletRequest request, HttpServletResponse response) {
		String externalID = request.getParameter("externalID");
		String conceptName = request.getParameter("conceptName");

		openMrsClient.printValues(externalID, conceptName);

		openMrsClient.lastTimeFulfilledDateTimeObs(externalID, conceptName);

		List<Patient> patientList = patientDAO.findAllPatients();
		
		Map<String, Object> modelMap = new TreeMap<String, Object>();
		modelMap.put("patients", patientList); //List of patients is for display purposes only
		
		ModelAndView mv = new ModelAndView("scheduleTrackingPage", modelMap);

		return mv;
	}

	public ModelAndView scheduleTracking(HttpServletRequest request, HttpServletResponse response) {
		
		List<Patient> patientList = patientDAO.findAllPatients();
		
		Map<String, Object> modelMap = new TreeMap<String, Object>();
		modelMap.put("patients", patientList); //List of patients is for display purposes only
		
		ModelAndView mv = new ModelAndView("scheduleTrackingPage", modelMap);

		return mv;
	}
}





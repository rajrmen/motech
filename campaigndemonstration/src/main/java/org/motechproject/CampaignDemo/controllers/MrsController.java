package org.motechproject.CampaignDemo.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.motechproject.CampaignDemo.dao.PatientAuthentication;
import org.motechproject.openmrs.Context;
import org.openmrs.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class MrsController extends MultiActionController {

	@Autowired
	private PatientAuthentication patients;
	
	@Autowired
	private Context motechMRScontext; 
	
	public ModelAndView mrs(HttpServletRequest request, HttpServletResponse response) {
		
		System.out.println("Doing stuff...");
		
		UserService userService = motechMRScontext.getUserService();
		
		if (userService == null) {
			System.out.println("User service null");
		}
		
		System.out.println("Proceeding with other things...");
		patients.login();
		
		return new ModelAndView("patientPage", null);
	}
	
	public ModelAndView add(HttpServletRequest request, HttpServletResponse response) {
		
		patients.addPerson();
		
		return new ModelAndView("patientPage", null);
	}
}

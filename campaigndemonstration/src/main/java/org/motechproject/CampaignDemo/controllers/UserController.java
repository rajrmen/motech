package org.motechproject.CampaignDemo.controllers;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.motechproject.CampaignDemo.dao.PatientDAO;
import org.motechproject.CampaignDemo.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;


public class UserController extends MultiActionController {

	@Autowired
	private PatientDAO patientDAO;
	
	public ModelAndView add(HttpServletRequest request, HttpServletResponse response) {
		
		List<Patient> patientList = null;
		
		String phoneNum = request.getParameter("phoneNum");
		String externalID = request.getParameter("externalId");
		
		if (phoneNum.length() == 0 || phoneNum.equals("") || phoneNum.trim().length() == 0) {
			//Don't register empty string IDs
		} else {
			patientList = patientDAO.findByExternalid(externalID);
			if (patientList.size() > 0) {
				Patient thePatient = patientList.get(0);
				thePatient.setPhoneNum(phoneNum);
				patientDAO.update(thePatient);
				System.out.println("Updated user");
			} else {
				patientDAO.add(new Patient(externalID, phoneNum));
				System.out.println("Added user");
			}
		}
		
		patientList = patientDAO.findAllPatients();
		
		Map<String, Object> modelMap = new TreeMap<String, Object>();
		modelMap.put("patients", patientList);
		
		ModelAndView mv = new ModelAndView("formPage", modelMap);
		
		return mv;
	}
	
	public ModelAndView remove(HttpServletRequest request, HttpServletResponse response) {
		
		String externalID = request.getParameter("externalId");
		
		patientDAO.removePatient(externalID);
		
		System.out.println("Removed user");
		
		List<Patient> patientList = patientDAO.findAllPatients();
		
		Map<String, Object> modelMap = new TreeMap<String, Object>();
		modelMap.put("patients", patientList);
		
		ModelAndView mv = new ModelAndView("formPage", modelMap);
		
		return mv;
	}
	
}

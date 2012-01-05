package org.motechproject.CampaignDemo.controllers;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.motechproject.CampaignDemo.dao.PatientDAO;
import org.motechproject.CampaignDemo.model.Patient;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
/**
 * A Spring controller for starting and stopping campaigns based on an external ID.
 * 
 * The PatientDAO is used only to display the list of registered users
 * 
 * @author Russell Gillen
 *
 */

public class CampaignController extends MultiActionController {

	@Autowired
	private PatientDAO patientDAO;
	
	@Autowired
	private MessageCampaignService service;
	
	public CampaignController(MessageCampaignService service, PatientDAO patientDAO) {
		this.patientDAO = patientDAO;
		this.service = service;
	}

	public ModelAndView start(HttpServletRequest request, HttpServletResponse response) {

		
		String externalId = request.getParameter("externalId");
		String campaignName = "Cron based Message Program"; //Campaign name is required, this could instead be provided as a parameter
		
		CampaignRequest campaignRequest = new CampaignRequest();
		campaignRequest.setCampaignName(campaignName);
		campaignRequest.setExternalId(externalId);
		
		service.startFor(campaignRequest);

		List<Patient> patientList = patientDAO.findAllPatients(); 
		
		Map<String, Object> modelMap = new TreeMap<String, Object>();
		modelMap.put("patients", patientList); //List of patients is for display purposes only
		
		ModelAndView mv = new ModelAndView("formPage", modelMap);
		
		return mv;
	}
	
	public ModelAndView stop(HttpServletRequest request, HttpServletResponse response) {
		
		String externalId = request.getParameter("externalId");
		String campaignName = "Cron based Message Program"; //Campaign name is required, this could instead be provided as a parameter
		
		CampaignRequest campaignRequest = new CampaignRequest();
		campaignRequest.setCampaignName(campaignName);
		campaignRequest.setExternalId(externalId);
		
		service.stopAll(campaignRequest); //Stops ALL messages associated with the specific campaign and specific external id
		//To stop a specific message, instead call service.stopFor(campaignRequest, messageKey) with the provided message key as a parameter
		
		List<Patient> patientList = patientDAO.findAllPatients();
		
		Map<String, Object> modelMap = new TreeMap<String, Object>();
		modelMap.put("patients", patientList); //List of patients is for display purposes only
		
		ModelAndView mv = new ModelAndView("formPage", modelMap);
		
		return mv;
	}

}

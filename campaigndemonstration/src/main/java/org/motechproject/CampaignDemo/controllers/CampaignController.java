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


public class CampaignController extends MultiActionController {

	@Autowired
	private PatientDAO patientDAO;
	
	@Autowired
	private MessageCampaignService service;

	public ModelAndView start(HttpServletRequest request, HttpServletResponse response) {

		String requestId = request.getParameter("externalId");
		String campaignName = "Cron based Message Program";
		
		CampaignRequest campaignRequest = new CampaignRequest();
		campaignRequest.setCampaignName(campaignName);
		campaignRequest.setExternalId(requestId);
		
		service.startFor(campaignRequest);
		
		System.out.println("Started campaign for id " + requestId);
		
		List<Patient> patientList = patientDAO.findAllPatients();
		
		Map<String, Object> modelMap = new TreeMap<String, Object>();
		modelMap.put("patients", patientList);
		
		ModelAndView mv = new ModelAndView("formPage", modelMap);
		
		return mv;
	}
	
	public ModelAndView stop(HttpServletRequest request, HttpServletResponse response) {
		
		String requestId = request.getParameter("externalId");
		String campaignName = "Cron based Message Program";
		
		CampaignRequest campaignRequest = new CampaignRequest();
		campaignRequest.setCampaignName(campaignName);
		campaignRequest.setExternalId(requestId);
		
		service.stopAll(campaignRequest);
		//service.stopFor(campaignRequest, "cron-message");
		System.out.println("Stopped campaign for " + requestId);
		
		List<Patient> patientList = patientDAO.findAllPatients();
		
		Map<String, Object> modelMap = new TreeMap<String, Object>();
		modelMap.put("patients", patientList);
		
		ModelAndView mv = new ModelAndView("formPage", modelMap);
		
		return mv;
	}

}

package org.motechproject.commcare.web;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.motechproject.commcare.domain.CaseXml;
import org.motechproject.commcare.domain.CommcareUser;
import org.motechproject.commcare.events.events.CaseEvent;
import org.motechproject.commcare.exception.CaseParserException;
import org.motechproject.commcare.parser.CommcareCaseParser;
import org.motechproject.commcare.service.CommcareCaseService;
import org.motechproject.commcare.service.CommcareUserService;
import org.motechproject.gateway.OutboundEventGateway;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Controller
public class CommcareController  {
	
	@Value("#{case_event_strategy['case.events.send.with.all.data']}")
	private String caseEventStrategy;
	
	private static final String FULL_DATA_EVENT = "full";
	private static final String PARTIAL_DATA_EVENT = "partial";
	private static final String MINIMAL_DATA_EVENT = "minimal";
	
	private OutboundEventGateway outboundEventGateway;

	private Logger logger = LoggerFactory.getLogger((this.getClass()));

	private String getRequestBodyAsString(HttpServletRequest request) throws IOException {
		BufferedReader reader = request.getReader();
		boolean end = false;
		String forwardedRequest = "";
		while (!end) {
			String line = reader.readLine();
			if (line == null) { end = true; } else {
				forwardedRequest += line;
			}
		}
		System.out.println(forwardedRequest);
		return forwardedRequest;
	}
	

	@RequestMapping("/forms")
	public ModelAndView testForms(HttpServletRequest request, HttpServletResponse response) {
		String formXml = "";
		
		try {
			formXml = getRequestBodyAsString(request);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return null;
	}


	@RequestMapping("/cases")
	public ModelAndView testCases(HttpServletRequest request, HttpServletResponse response) {
        String caseXml = "";

		try {
			caseXml = getRequestBodyAsString(request);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		CommcareCaseParser<CaseXml> parser = new CommcareCaseParser<CaseXml>(CaseXml.class, caseXml);
		

		CaseXml caseInstance = null;
		
		try {
			caseInstance = (CaseXml) parser.parseCase();
		} catch (CaseParserException e) {

		}

		if (caseInstance != null) {
			System.out.println("Case not null");
			
			CaseEvent caseEvent = new CaseEvent(caseInstance.getCase_id());
			
			MotechEvent motechCaseEvent = null;
			
			if (caseEventStrategy.equals(FULL_DATA_EVENT) || caseEventStrategy.equals(PARTIAL_DATA_EVENT)) {
				caseEvent = caseEvent.eventFromCase(caseInstance);
				motechCaseEvent = caseEvent.toMotechEvenWithData();
			} else {
				motechCaseEvent = caseEvent.toMotechEventWihoutData();
			}
			
			System.out.println("About to call...");
			
			outboundEventGateway.sendEventMessage(motechCaseEvent);
		} else {
			System.out.println("Case null");
		}
		return null;
	}

	public void setOutboundEventGateway(OutboundEventGateway outboundEventGateway) {
		this.outboundEventGateway = outboundEventGateway;
	}

	


}
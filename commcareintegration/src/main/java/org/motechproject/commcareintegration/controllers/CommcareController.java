package org.motechproject.commcareintegration.controllers;

import java.io.BufferedReader;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.motechproject.commcare.domain.Case;
import org.motechproject.commcare.parser.CommcareCaseParser;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class CommcareController implements Controller {

	@Override
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		System.out.println("Starting commcare controller call");
		BufferedReader reader = request.getReader();
		boolean end = false;
		String forwardedRequest = "";
		while (!end) {
			String line = reader.readLine();
			if (line == null) { end = true; } else {
				forwardedRequest += line;
			}
		}
        CommcareCaseParser<Case> parser = new CommcareCaseParser<Case>(Case.class,forwardedRequest);
		Case caseInstance = parser.parseCase();
		System.out.println("Have the case: ");
		System.out.println("Case type id: " + caseInstance.getCase_type_id());
		System.out.println("Case userId " + caseInstance.getUser_id());
        System.out.println(forwardedRequest);
		
		return new ModelAndView();
	}

}

package org.motechproject.commcareintegration.controllers;

import java.io.BufferedReader;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class CommcareController implements Controller {

	@Override
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Enumeration<?> enums = request.getAttributeNames();
		Map<String, String[]> map = request.getParameterMap();
		System.out.println("Starting commcare controller call");
		BufferedReader reader = request.getReader();
		boolean end = false;
		while (!end) {
			String line = reader.readLine();
			System.out.println(line);
			if (line == null) { end = true; }
		}
		return null;
	}

}

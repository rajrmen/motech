package org.motechproject.ScheduleTrackingDemo.controllers;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller to distribute the Mobile Application
 */
@Controller
@RequestMapping(value="mobileapp")
public class MobileAppController {
	Logger logger = Logger.getLogger(MobileAppController.class);

	@RequestMapping(value="/motech-mforms-tiny.jad", method=RequestMethod.GET)
	public void getMobileApplicationJad(HttpServletResponse response) {
		writeFile(response, "motech-mforms-tiny.jad");
	}
	
	@RequestMapping(value="/motech-mforms-tiny-0.8.0.jar", method=RequestMethod.GET)
	public void getMobileApplication(HttpServletResponse response) {
		writeFile(response, "motech-mforms-tiny-0.8.0.jar");
	}

	private void writeFile(HttpServletResponse response, String file) {
		try {
			InputStream is = new ClassPathResource("mobile/" + file).getInputStream();
			response.setStatus(200);
			IOUtils.copy(is, response.getOutputStream());
			response.flushBuffer();
		} catch (IOException e) {
			logger.warn("Failed to write file");
			throw new RuntimeException("Error reading mobile app");
		}
	}
}

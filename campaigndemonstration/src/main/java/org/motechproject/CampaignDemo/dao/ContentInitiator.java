package org.motechproject.CampaignDemo.dao;

import org.motechproject.cmslite.api.repository.AllStringContents;
import org.motechproject.cmslite.api.model.StringContent;
import org.springframework.beans.factory.annotation.Autowired;



public class ContentInitiator {

	@Autowired 
	private AllStringContents stringContent;
	
	public void bootstrap() {
		StringContent content = stringContent.getContent("en", "cron-message");
		if (content == null) {
			System.out.println("Adding content to DB");
			stringContent.add(new StringContent("en", "cron-message", "demo.xml"));
		}
	}

	
	
}

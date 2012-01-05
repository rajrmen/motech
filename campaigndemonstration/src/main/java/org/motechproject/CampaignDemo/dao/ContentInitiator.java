package org.motechproject.CampaignDemo.dao;

import org.motechproject.cmslite.api.repository.AllStringContents;
import org.motechproject.cmslite.api.model.StringContent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Class to initialize CMSlite content in the DB upon server startup
 * Currently, the content is the name of the voice XML file to be run by Voxeo
 * @author Russell Gillen
 *
 */

public class ContentInitiator {

	@Autowired 
	private AllStringContents stringContent;
	
	public void bootstrap() {
		StringContent content = stringContent.getContent("en", "cron-message");
		if (content == null) {
			stringContent.add(new StringContent("en", "cron-message", "demo.xml"));
		}
	}

	
	
}

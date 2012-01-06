package org.motechproject.CampaignDemo.dao;

import java.io.InputStream;

import org.motechproject.cmslite.api.repository.AllStreamContents;
import org.motechproject.cmslite.api.repository.AllStringContents;
import org.motechproject.cmslite.api.model.CMSLiteException;
import org.motechproject.cmslite.api.model.StreamContent;
import org.motechproject.cmslite.api.model.StringContent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Class to initialize CMSlite content in the DB upon server startup
 * Currently, the content is the name of the voice XML file to be run by Voxeo
 * @author Russell Gillen
 *
 */

public class ContentInitiator {

	/**
	 * Defined in the motech-cmslite-api module, available from applicationCmsLiteApi.xml import
	 */
	@Autowired 
	private AllStringContents stringContent;
	
	/**
	 * Defined in the motech-cmslite-api module, available from applicationCmsLiteApi.xml import
	 */
	@Autowired
	private AllStreamContents streamContent;
	
	public void bootstrap() {
        InputStream inputStreamToResource1 = this.getClass().getResourceAsStream("/background.wav");
        StreamContent file1 = new StreamContent("en", "greeting", inputStreamToResource1, "checksum1", "audio/wav");
        try {
			streamContent.addContent(file1);
		} catch (CMSLiteException e) {
		}
		
		StringContent content = stringContent.getContent("en", "cron-message");
		if (content == null) { //Content not already in DB, add it
			try {
				stringContent.addContent(new StringContent("en", "cron-message", "english/demo.xml"));
			} catch (CMSLiteException e) {
			}
		}
	}

	
}

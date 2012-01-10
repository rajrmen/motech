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
	
	public void bootstrap() throws CMSLiteException {
        InputStream inputStreamToResource1 = this.getClass().getResourceAsStream("/cronmessage.wav");
        StreamContent file1 = new StreamContent("en", "test", inputStreamToResource1, "checksum1", "audio/wav");
        
        InputStream inputStreamToResource2 = this.getClass().getResourceAsStream("/demo1.wav");
        StreamContent file2 = new StreamContent("en", "greeting", inputStreamToResource2, "checksum1", "audio/wav");
        InputStream inputStreamToResource3 = this.getClass().getResourceAsStream("/demo2.wav");
        StreamContent file3 = new StreamContent("en", "greeting2", inputStreamToResource3, "checksum1", "audio/wav");
        InputStream inputStreamToResource4 = this.getClass().getResourceAsStream("/demo3.wav");
        StreamContent file4 = new StreamContent("en", "greeting3", inputStreamToResource4, "checksum1", "audio/wav");
        InputStream inputStreamToResource5 = this.getClass().getResourceAsStream("/demo4.wav");
        StreamContent file5 = new StreamContent("en", "greeting4", inputStreamToResource5, "checksum1", "audio/wav");
        InputStream inputStreamToResource6 = this.getClass().getResourceAsStream("/demo5.wav");
        StreamContent file6 = new StreamContent("en", "greeting5", inputStreamToResource6, "checksum1", "audio/wav");
        try {
			streamContent.addContent(file1);
			streamContent.addContent(file2);
			streamContent.addContent(file3);
			streamContent.addContent(file4);
			streamContent.addContent(file5);
			streamContent.addContent(file6);
		} catch (CMSLiteException e) {
		}
		
		StringContent content = stringContent.getContent("en", "cron-message");
		StringContent content2 = stringContent.getContent("en", "child-info-week-1");
		StringContent content3 = stringContent.getContent("en", "child-info-week-1a");
		StringContent content4 = stringContent.getContent("en", "child-info-week-1b");
		if (content == null) { //Content not already in DB, add it
			try {
				stringContent.addContent(new StringContent("en", "cron-message", "english/cron.xml"));
			} catch (CMSLiteException e) {
			}
		}
		stringContent.addContent(new StringContent("en", "child-info-week-1", "english/demo.xml"));
		stringContent.addContent(new StringContent("en", "child-info-week-1a", "english/demo2.xml"));
		stringContent.addContent(new StringContent("en", "child-info-week-1b", "english/demo3.xml"));
		stringContent.addContent(new StringContent("en", "child-info-week-1c", "english/demo4.xml"));
		stringContent.addContent(new StringContent("en", "child-info-week-1d", "english/demo5.xml"));

	}

	
}

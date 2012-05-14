package org.motechproject.openmrs.rest.impl;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.motechproject.mrs.services.MRSException;
import org.motechproject.openmrs.rest.HttpException;
import org.motechproject.openmrs.rest.RestfulClient;
import org.motechproject.openmrs.rest.util.OpenMrsUrlHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MRSConceptAdapterImpl {
	private static final Logger logger = LoggerFactory.getLogger(MRSConceptAdapterImpl.class);
	
	private final RestfulClient restfulClient;
	private final OpenMrsUrlHolder urlHolder;
	private final Map<String, String> conceptCache = new HashMap<String, String>();
	
	@Autowired
	public MRSConceptAdapterImpl(RestfulClient restfulClient, OpenMrsUrlHolder urlHolder) {
		this.restfulClient = restfulClient;
		this.urlHolder = urlHolder;
	}
	
	public void clearCachedConcepts() {
		conceptCache.clear();
	}
	
	public String resolveConceptUuidFromConceptName(String conceptName) {
		if (conceptCache.containsKey(conceptName)) {
			return conceptCache.get(conceptName);
		}
		
		try {
			String encodedConceptName = URLEncoder.encode(conceptName, "UTF-8");
			JsonNode response = restfulClient.getEntityByJsonNode(urlHolder
					.getConceptSearchByName(encodedConceptName));
			JsonNode results = response.get("results");
			if (results.size() == 0) {
				logger.error("No concept found with name: " + conceptName + " (" + encodedConceptName + ")");
				throw new MRSException(new RuntimeException(
						"Can't create an encounter because no concept was found with name: " + conceptName));
			} else if (results.size() > 1) {
				logger.warn("Found more than 1 concept with name: " + conceptName + " (" + encodedConceptName + ")");
			}

			String conceptUuid = results.get(0).get("uuid").getValueAsText();
			conceptCache.put(conceptName, conceptUuid);
			return conceptUuid;
		} catch (UnsupportedEncodingException e) {
			logger.error("Could not URL Encode the concept name: " + conceptName);
			throw new MRSException(e);
		} catch (HttpException e) {
			logger.error("There was an error retrieving the uuid of the concept with concept name: " + conceptName);
			throw new MRSException(e);
		}
	}
}

package org.motechproject.openmrs.rest.url;

import java.net.URI;
import java.net.URISyntaxException;

import org.motechproject.mrs.services.MRSException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;

@Component
public class OpenMrsLocationUrlHolder implements InitializingBean {
	
	private String openmrsUrl;

	private final static String FACILITY_PATH = "/ws/rest/v1/location";
	private final static String FACILITY_LIST_ALL_PATH = FACILITY_PATH + "?v=full";
	private final static String FACILITY_LIST_ALL_BY_NAME_PATH = FACILITY_PATH + "?q={name}&v=full";
	private final static String FACILITY_FIND_BY_UUID_PATH = FACILITY_PATH + "/{uuid}";
	
	private URI facilityListUri;
	private URI facilityCreateUri;
	private UriTemplate facilityListUriTemplate;
	private UriTemplate facilityFindUriTemplate;	
	
	@Autowired
	public OpenMrsLocationUrlHolder(@Value("${openmrs.url}") String openmrsUrl) {
		this.openmrsUrl = openmrsUrl;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		createFacilityUris();		
	}	

	private void createFacilityUris() {
		facilityListUriTemplate = new UriTemplate(openmrsUrl + FACILITY_LIST_ALL_BY_NAME_PATH);
		facilityFindUriTemplate = new UriTemplate(openmrsUrl + FACILITY_FIND_BY_UUID_PATH);		
		
		try {
			facilityListUri = new URI(openmrsUrl + FACILITY_LIST_ALL_PATH);
			facilityCreateUri = new URI(openmrsUrl + FACILITY_PATH);
		} catch (URISyntaxException e) {
			throw new MRSException(e);
		}		
	}

	public URI getFacilityListUri() {
		return facilityListUri;
	}

	public URI getFacilityCreateUri() {
		return facilityCreateUri;
	}

	public URI getFacilityListUri(String facilityName) {
		return facilityListUriTemplate.expand(facilityName);
	}

	public URI getFacilityFindUri(String facilityUuid) {
		return facilityFindUriTemplate.expand(facilityUuid);
	}
}

package org.motechproject.openmrs.rest.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.services.MRSException;
import org.motechproject.mrs.services.MRSFacilityAdapter;
import org.motechproject.openmrs.rest.HttpException;
import org.motechproject.openmrs.rest.RestfulClient;
import org.motechproject.openmrs.rest.url.OpenMrsLocationUrlHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MRSFacilityAdapterImpl implements MRSFacilityAdapter {

	private RestfulClient restClient;
	private OpenMrsLocationUrlHolder urlHolder;

	@Autowired
	public MRSFacilityAdapterImpl(RestfulClient restClient, OpenMrsLocationUrlHolder urlHolder) {
		this.restClient = restClient;
		this.urlHolder = urlHolder;
	}

	@Override
	public List<MRSFacility> getFacilities() {
		JsonNode node = getJsonNode(urlHolder.getFacilityListUri());

		List<MRSFacility> facilities = new ArrayList<MRSFacility>();
		for (int i = 0; i < node.size(); i++) {
			MRSFacility facility = convertJsonToMrsFacility(node.get(i));
			facilities.add(facility);
		}

		return facilities;
	}

	private MRSFacility convertJsonToMrsFacility(JsonNode location) {
		MRSFacility facility = new MRSFacility(location.get("uuid").asText(), location.get("name").asText(), location
		        .get("country").asText(), location.get("address6").asText(), location.get("countyDistrict").asText(),
		        location.get("stateProvince").asText());
		return facility;
	}

	@Override
	public List<MRSFacility> getFacilities(String locationName) {
		JsonNode obj = getJsonNode(urlHolder.getFacilityListUri(locationName));
		
		JsonNode results = obj.get("results");

		List<MRSFacility> facilities = new ArrayList<MRSFacility>();
		for (int i = 0; i < results.size(); i++) {
			MRSFacility facility = convertJsonToMrsFacility(results.get(i));
			facilities.add(facility);
		}

		return facilities;
	}

	private JsonNode getJsonNode(URI uri) {
		JsonNode obj;
		try {
			obj = restClient.getEntityByJsonNode(uri);
		} catch (HttpException e) {
			throw new MRSException(e);
		}
		return obj;
	}

	@Override
	public MRSFacility getFacility(String facilityId) {
		JsonNode obj = getJsonNode(urlHolder.getFacilityFindUri(facilityId));

		return convertJsonToMrsFacility(obj);
	}

	@Override
	public MRSFacility saveFacility(MRSFacility facility) {
		ObjectNode node = JsonNodeFactory.instance.objectNode();
		node.put("name", facility.getName());
		node.put("country", facility.getCountry());
		node.put("address6", facility.getRegion());
		node.put("countyDistrict", facility.getCountyDistrict());
		node.put("stateProvince", facility.getStateProvince());

		JsonNode result;
		try {
			result = restClient.postForJsonNode(urlHolder.getFacilityCreateUri(), node);
		} catch (HttpException e) {
			throw new MRSException(e);
		}

		facility.setId(result.get("uuid").asText());

		return facility;
	}
}

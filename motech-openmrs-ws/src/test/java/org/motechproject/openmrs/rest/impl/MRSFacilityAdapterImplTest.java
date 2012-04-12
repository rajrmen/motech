package org.motechproject.openmrs.rest.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.net.URI;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.openmrs.rest.HttpException;
import org.motechproject.openmrs.rest.RestfulClient;
import org.motechproject.openmrs.rest.url.OpenMrsLocationUrlHolder;

public class MRSFacilityAdapterImplTest {

	private static final String FACILITY_STATE = "Facility State";
	private static final String FACILITY_DISTRICT = "Facility District";
	private static final String REGION = "Region";
	private static final String FACILITY_COUNTRY = "Facility Country";
	private static final String LOCATION_NAME = "Location Name";
	private static final String UUID = "AAABBBCCC";

	@Mock 
	RestfulClient client;
	
	MRSFacilityAdapterImpl impl;
	
	@Before
	public void setUp() {
		initMocks(this);
		impl = new MRSFacilityAdapterImpl(client, new OpenMrsLocationUrlHolder("http://www.openmrs.org"));
	}
	
	@Test
	public void shouldMapJsonObjectIntoMrsFacility() throws HttpException {
		JsonNode facilityObj = getJsonObject();
		ArrayNode facilities = JsonNodeFactory.instance.arrayNode();
		facilities.add(facilityObj);
		
		when(client.getEntityByJsonNode(any(URI.class))).thenReturn(facilities);
		
		List<MRSFacility> mrsFacilities = impl.getFacilities();
		
		assertEquals(1, mrsFacilities.size());
		
		MRSFacility fac = mrsFacilities.get(0);
		
		assertEquals(UUID, fac.getId());
		assertEquals(LOCATION_NAME, fac.getName());
		assertEquals(FACILITY_COUNTRY, fac.getCountry());
		assertEquals(REGION, fac.getRegion());
		assertEquals(FACILITY_DISTRICT, fac.getCountyDistrict());
		assertEquals(FACILITY_STATE, fac.getStateProvince());
	}
	
	@Test
	public void shouldSetUuidOnSave() throws HttpException {
		JsonNode facilityObj = getJsonObject();
		
		when(client.postForJsonNode(any(URI.class), any(JsonNode.class))).thenReturn(facilityObj);
		
		MRSFacility facility = new MRSFacility(LOCATION_NAME, FACILITY_COUNTRY, REGION, FACILITY_DISTRICT, FACILITY_STATE);
		impl.saveFacility(facility);
		
		assertNotNull(facility.getId());
		assertEquals(UUID, facility.getId());
	}
	
	JsonNode getJsonObject() {
		ObjectNode node = JsonNodeFactory.instance.objectNode();
		node.put("uuid", UUID);
		node.put("name", LOCATION_NAME);
		node.put("country", FACILITY_COUNTRY);
		node.put("address6", REGION);
		node.put("countyDistrict", FACILITY_DISTRICT);
		node.put("stateProvince", FACILITY_STATE);
		
		return node;
	}
}

package org.motechproject.openmrs.rest.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.codehaus.jackson.JsonNode;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.openmrs.rest.HttpException;
import org.motechproject.openmrs.rest.RestfulClient;
import org.motechproject.openmrs.rest.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;

@Component
public class MRSObservationAdapterImpl {

	private final MRSPatientAdapter patientAdapter;
	private final RestfulClient restfulClient;
	private final MRSConceptAdapterImpl conceptAdapter;

	@Autowired
	public MRSObservationAdapterImpl(MRSPatientAdapter patientAdapter, RestfulClient restfulClient, MRSConceptAdapterImpl conceptAdapter) {
		this.patientAdapter = patientAdapter;
		this.restfulClient = restfulClient;
		this.conceptAdapter = conceptAdapter;
	}

	public List<MRSObservation> getMRSObservationsByMotechPatientIdAndConceptName(String motechId, String conceptName) {
		Validate.notEmpty(motechId, "Motech id cannot be empty");
		Validate.notEmpty(conceptName, "Concept name cannot be empty");

		List<MRSObservation> obs = new ArrayList<MRSObservation>();
		MRSPatient patient = patientAdapter.getPatientByMotechId(motechId);
		if (patient == null) {
			return obs;
		}
		
		String conceptUuid = conceptAdapter.resolveConceptUuidFromConceptName(conceptName);
		
		try {
			JsonNode results = restfulClient.getEntityByJsonNode(
					new UriTemplate("http://localhost:8092/openmrs/ws/rest/v1/obs?patient={uuid}").expand(patient.getId()))
					.get("results");
			for(int i = 0; i < results.size(); i++) {
				JsonNode obj = results.get(i);
				if (!conceptUuid.equals(obj.get("concept").get("uuid").asText())) {
					continue;
				}
				MRSObservation ob = new MRSObservation(obj.get("uuid").asText(), DateUtil.parseOpenMrsDate(obj.get(
						"obsDatetime").asText()), conceptName, obj.get("value").asText());
				
				obs.add(ob);
			}
		} catch (HttpException e) {
		} catch (ParseException e) {
		}
		
		return obs;
	}
}

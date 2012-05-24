package org.motechproject.openmrs.rest.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.motechproject.mrs.exception.MRSException;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.services.MRSEncounterAdapter;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.openmrs.rest.HttpException;
import org.motechproject.openmrs.rest.RestfulClient;
import org.motechproject.openmrs.rest.util.DateUtil;
import org.motechproject.openmrs.rest.util.JsonConverterUtil;
import org.motechproject.openmrs.rest.util.OpenMrsUrlHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("encounterAdapter")
public class MRSEncounterAdapterImpl implements MRSEncounterAdapter {
	private static final Logger logger = LoggerFactory.getLogger(MRSEncounterAdapterImpl.class);

	private final MRSPatientAdapter patientAdapter;
	private final RestfulClient restfulClient;
	private final OpenMrsUrlHolder urlHolder;
	private final MRSPersonAdapterImpl personAdapter;
	private final MRSConceptAdapterImpl conceptAdapter;

	@Autowired
	public MRSEncounterAdapterImpl(RestfulClient restfulClient, MRSPatientAdapter patientAdapter,
			OpenMrsUrlHolder encounterUrl, MRSPersonAdapterImpl personAdapter, MRSConceptAdapterImpl conceptAdapter) {
		this.restfulClient = restfulClient;
		this.patientAdapter = patientAdapter;
		this.urlHolder = encounterUrl;
		this.personAdapter = personAdapter;
		this.conceptAdapter = conceptAdapter;
	}

	@Override
	public MRSEncounter createEncounter(MRSEncounter encounter) {
		ObjectNode encounterObj = JsonNodeFactory.instance.objectNode();
		encounterObj.put("location", encounter.getFacility().getId());
		encounterObj.put("encounterType", encounter.getEncounterType());
		encounterObj.put("encounterDatetime", DateUtil.formatToOpenMrsDate(encounter.getDate()));
		encounterObj.put("patient", encounter.getPatient().getId());
		encounterObj.put("provider", encounter.getProvider().getId());
		
		// web services do not currently allow creator property
		//encounterObj.put("creator", encounter.getCreator().getId());
		
		encounterObj.put("obs", observationsToJson(encounter.getObservations()));
		try {
			JsonNode response = restfulClient.postForJsonNode(urlHolder.getEncounterPath(), encounterObj);
			return new MRSEncounter(response.get("uuid").getValueAsText(), encounter.getProvider(), encounter.getCreator(),
					encounter.getFacility(), encounter.getDate(), encounter.getPatient(), encounter.getObservations(),
					encounter.getEncounterType());
		} catch (HttpException e) {
			logger.error("Could not create encounter: " + e.getMessage());
			throw new MRSException(e);
		}
	}

	private ArrayNode observationsToJson(Set<MRSObservation> observations) {
		ArrayNode obsArray = JsonNodeFactory.instance.arrayNode();
		for (MRSObservation obs : observations) {
			ObjectNode obsObj = JsonNodeFactory.instance.objectNode();

			obsObj.put("concept", conceptAdapter.resolveConceptUuidFromConceptName(obs.getConceptName()));
			obsObj.put("value", obs.getValue().toString());
			obsObj.put("obsDatetime", DateUtil.formatToOpenMrsDate(obs.getDate()));
			if (CollectionUtils.isNotEmpty(obs.getDependantObservations())) {
				obsObj.put("groupMembers", observationsToJson(obs.getDependantObservations()));
			}

			obsArray.add(obsObj);
		}

		return obsArray;
	}

	public List<MRSEncounter> getAllEncountersByPatientMotechId(String motechId) {
		List<MRSEncounter> encounters = new ArrayList<MRSEncounter>();
		MRSPatient patient = patientAdapter.getPatientByMotechId(motechId);

		if (patient != null) {
			encounters.addAll(getEncountersForPatient(patient));
		}

		return encounters;
	}

	private List<MRSEncounter> getEncountersForPatient(MRSPatient patient) {
		List<MRSEncounter> encounters = new ArrayList<MRSEncounter>();
		try {
			JsonNode resultObj = restfulClient.getEntityByJsonNode(urlHolder.getEncountersByPatientUuid(patient
					.getId()));
			JsonNode resultArray = resultObj.get("results");
			for (int i = 0; i < resultArray.size(); i++) {
				JsonNode encounterObj = resultArray.get(i);
				String encounterType = encounterObj.get("encounterType").get("name").getValueAsText();
				Date encounterDate = DateUtil.parseOpenMrsDate(encounterObj.get("encounterDatetime").getValueAsText());

				MRSFacility facility = JsonConverterUtil.convertJsonToMrsFacility(encounterObj.get("location"));

				MRSPerson provider = personAdapter.getPerson(encounterObj.get("provider").get("uuid").getValueAsText());

				//JsonNode creatorObj = getCreator(encounterObj.get("auditInfo").get("creator").get("uuid").asText());
				//JsonNode creatorPersonObj = getPerson(creatorObj.get("person").get("uuid").asText());
				//MRSPerson creatorPerson = JsonConverterUtil.convertJsonToMrsPerson(creatorPersonObj);
//				MRSUser user = new MRSUser().id(creatorObj.get("uuid").asText()).person(creatorPerson)
//						.systemId(creatorObj.get("systemId").asText()).userName(creatorObj.get("username").asText());

				MRSEncounter encounter = new MRSEncounter(encounterObj.get("uuid").getValueAsText(), provider, null, facility,
						encounterDate, patient, getObservations(encounterObj.get("obs")), encounterType);
				encounters.add(encounter);
			}
		} catch (HttpException e) {
			logger.error("Error retrieving encounters for patient: " + patient.getMotechId());
			throw new MRSException(e);
		} catch (ParseException e) {
			logger.error("Error parsing encounter date for patient: " + patient.getMotechId());
			throw new MRSException(e);
		}

		return encounters;
	}

	private JsonNode getCreator(String asText) {
		try {
			return restfulClient.getEntityByJsonNode(urlHolder.getCreatorByUuid(asText));
		} catch (HttpException e) {
			throw new MRSException(e);
		}
	}

	private Set<MRSObservation> getObservations(JsonNode observationArray) throws ParseException {
		Set<MRSObservation> observations = new HashSet<MRSObservation>();
		for (int i = 0; i < observationArray.size(); i++) {
			JsonNode obsObj = observationArray.get(i);
			observations.add(new MRSObservation(obsObj.get("uuid").getValueAsText(), DateUtil.parseOpenMrsDate(obsObj.get(
					"obsDatetime").getValueAsText()), obsObj.get("concept").get("display").getValueAsText(), obsObj.get("value")
					.getValueAsText()));
		}

		return observations;
	}

	@Override
	public MRSEncounter getLatestEncounterByPatientMotechId(String motechId, String encounterType) {
		List<MRSEncounter> previousEncounters = getAllEncountersByPatientMotechId(motechId);
		Iterator<MRSEncounter> encounterItr = previousEncounters.iterator();
		
		// filter out encounters with non matching encounterType
		while(StringUtils.isNotBlank(encounterType) && encounterItr.hasNext()) {
			MRSEncounter enc = encounterItr.next();
			if (!encounterType.equals(enc.getEncounterType())) {
				encounterItr.remove();
			}
		}
		
		MRSEncounter latestEncounter = null;
		for(MRSEncounter enc : previousEncounters) {
			if (latestEncounter == null) {
				latestEncounter = enc;
			} else {
				latestEncounter = enc.getDate().after(latestEncounter.getDate()) ? enc : latestEncounter;
			}
		}
		
		return latestEncounter;
	}
}

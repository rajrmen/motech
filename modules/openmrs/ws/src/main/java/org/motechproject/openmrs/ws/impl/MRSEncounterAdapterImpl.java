package org.motechproject.openmrs.ws.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.motechproject.mrs.services.EncounterAdapter;
import org.motechproject.mrs.services.PatientAdapter;
import org.motechproject.openmrs.model.OpenMRSEncounter;
import org.motechproject.openmrs.model.OpenMRSObservation;
import org.motechproject.openmrs.model.OpenMRSPatient;
import org.motechproject.openmrs.model.OpenMRSProvider;
import org.motechproject.openmrs.model.OpenMRSUser;
import org.motechproject.openmrs.ws.HttpException;
import org.motechproject.openmrs.ws.resource.EncounterResource;
import org.motechproject.openmrs.ws.resource.model.Concept;
import org.motechproject.openmrs.ws.resource.model.Encounter;
import org.motechproject.openmrs.ws.resource.model.Encounter.EncounterType;
import org.motechproject.openmrs.ws.resource.model.EncounterListResult;
import org.motechproject.openmrs.ws.resource.model.Location;
import org.motechproject.openmrs.ws.resource.model.Observation;
import org.motechproject.openmrs.ws.resource.model.Observation.ObservationValue;
import org.motechproject.openmrs.ws.resource.model.Patient;
import org.motechproject.openmrs.ws.resource.model.Person;
import org.motechproject.openmrs.ws.util.ConverterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component("encounterAdapter")
public class MRSEncounterAdapterImpl implements EncounterAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(MRSEncounterAdapterImpl.class);

    private final PatientAdapter patientAdapter;
    private final MRSPersonAdapterImpl personAdapter;
    private final MRSConceptAdapterImpl conceptAdapter;
    private final EncounterResource encounterResource;

    @Autowired
    public MRSEncounterAdapterImpl(EncounterResource encounterResource, PatientAdapter patientAdapter,
            MRSPersonAdapterImpl personAdapter, MRSConceptAdapterImpl conceptAdapter) {
        this.encounterResource = encounterResource;
        this.patientAdapter = patientAdapter;
        this.personAdapter = personAdapter;
        this.conceptAdapter = conceptAdapter;
    }

    @Override
    public org.motechproject.mrs.domain.Encounter createEncounter(org.motechproject.mrs.domain.Encounter encounter) {
        validateEncounter((org.motechproject.mrs.domain.Encounter) encounter);

        // OpenMRS expects the observations to reference a concept uuid rather
        // than just a concept name. Attempt to map all concept names to concept
        // uuid's for each of the observations
        Set<OpenMRSObservation> updatedObs = resolveConceptUuidForConceptNames((Set<OpenMRSObservation>) encounter.getObservations());
        org.motechproject.mrs.domain.Encounter encounterCopy = new OpenMRSEncounter.MRSEncounterBuilder().withId(encounter.getEncounterId())
                .withProvider(encounter.getProvider()).withCreator((OpenMRSUser) encounter.getCreator())
                .withFacility(encounter.getFacility()).withDate(encounter.getDate().toDate())
                .withPatient(encounter.getPatient()).withObservations(updatedObs)
                .withEncounterType(encounter.getEncounterType()).build();

        Encounter converted = toEncounter(encounterCopy);
        Encounter saved = null;
        try {
            saved = encounterResource.createEncounter(converted);
        } catch (HttpException e) {
            LOGGER.error("Could not create encounter: " + e.getMessage());
            return null;
        }

        return new OpenMRSEncounter.MRSEncounterBuilder().withId(saved.getUuid()).withProvider((OpenMRSProvider) encounter.getProvider())
                .withCreator((OpenMRSUser) encounter.getCreator()).withFacility(encounter.getFacility())
                .withDate(encounter.getDate().toDate()).withPatient((OpenMRSPatient) encounter.getPatient())
                .withObservations(encounter.getObservations()).withEncounterType(encounter.getEncounterType()).build();
    }

    private void validateEncounter(org.motechproject.mrs.domain.Encounter encounter) {
        Validate.notNull(encounter, "Encounter cannot be null");
        Validate.notNull(encounter.getPatient(), "Patient cannot be null");
        Validate.notEmpty(encounter.getPatient().getPatientId(), "Patient must have an id");
        Validate.notNull(encounter.getDate(), "Encounter Date cannot be null");
        Validate.notEmpty(encounter.getEncounterType(), "Encounter type cannot be empty");
    }

    private Encounter toEncounter(org.motechproject.mrs.domain.Encounter encounter) {
        Encounter converted = new Encounter();
        converted.setEncounterDatetime(encounter.getDate().toDate());

        EncounterType encounterType = new EncounterType();
        encounterType.setName(encounter.getEncounterType());
        converted.setEncounterType(encounterType);

        Location location = new Location();
        location.setUuid(encounter.getFacility().getFacilityId());
        converted.setLocation(location);

        Patient patient = new Patient();
        patient.setUuid(encounter.getPatient().getPatientId());
        converted.setPatient(patient);

        Person person = new Person();
        person.setUuid(encounter.getProvider().getProviderId());
        converted.setProvider(person);

        converted.setObs(convertToObservations(encounter.getObservations()));

        return converted;
    }

    private List<Observation> convertToObservations(Set<? extends org.motechproject.mrs.domain.Observation> observations) {
        List<Observation> obs = new ArrayList<>();

        for (org.motechproject.mrs.domain.Observation observation : observations) {
            Observation ob = new Observation();
            ob.setObsDatetime(observation.getDate().toDate());

            Concept concept = new Concept();
            concept.setDisplay(observation.getConceptName());
            ob.setConcept(concept);

            ObservationValue value = new ObservationValue();
            value.setDisplay(observation.getValue().toString());
            ob.setValue(value);

            if (CollectionUtils.isNotEmpty(observation.getDependantObservations())) {
                ob.setGroupsMembers(convertToObservations(observation.getDependantObservations()));
            }

            obs.add(ob);
        }

        return obs;
    }

    private Set<OpenMRSObservation> resolveConceptUuidForConceptNames(Set<OpenMRSObservation> originalObservations) {
        Set<OpenMRSObservation> updatedObs = new HashSet<OpenMRSObservation>();
        for (OpenMRSObservation observation : originalObservations) {
            String conceptUuid = conceptAdapter.resolveConceptUuidFromConceptName(observation.getConceptName());
            if (CollectionUtils.isNotEmpty(observation.getDependantObservations())) {
                resolveConceptUuidForConceptNames(observation.getDependantObservations());
            }
            updatedObs.add(new OpenMRSObservation(observation.getId(), observation.getDate().toDate(), conceptUuid, observation
                    .getValue()));
        }

        return updatedObs;
    }

    @Override
    public org.motechproject.mrs.domain.Encounter getLatestEncounterByPatientMotechId(String motechId, String encounterType) {
        Validate.notEmpty(motechId, "MoTeCH Id cannot be empty");

        List<org.motechproject.mrs.domain.Encounter> previousEncounters = getAllEncountersByPatientMotechId(motechId);

        removeEncounters(previousEncounters, encounterType);

        org.motechproject.mrs.domain.Encounter latestEncounter = null;
        for (org.motechproject.mrs.domain.Encounter enc : previousEncounters) {
            if (latestEncounter == null) {
                latestEncounter = enc;
            } else {
                latestEncounter = enc.getDate().isAfter(latestEncounter.getDate()) ? enc : latestEncounter;
            }
        }

        return (org.motechproject.mrs.domain.Encounter) latestEncounter;
    }

    public List<org.motechproject.mrs.domain.Encounter> getAllEncountersByPatientMotechId(String motechId) {
        Validate.notEmpty(motechId, "MoTeCH Id cannot be empty");

        List<org.motechproject.mrs.domain.Encounter> encounters = new ArrayList<org.motechproject.mrs.domain.Encounter>();
        org.motechproject.mrs.domain.Patient patient = patientAdapter.getPatientByMotechId(motechId);

        if (patient != null) {
            encounters.addAll(getEncountersForPatient((OpenMRSPatient) patient));
        }

        return encounters;
    }

    private List<org.motechproject.mrs.domain.Encounter> getEncountersForPatient(OpenMRSPatient patient) {
        EncounterListResult result = null;
        try {
            result = encounterResource.queryForAllEncountersByPatientId(patient.getPatientId());
        } catch (HttpException e) {
            LOGGER.error("Error retrieving encounters for patient: " + patient.getPatientId());
            return Collections.emptyList();
        }

        if (result.getResults().size() == 0) {
            return Collections.emptyList();
        }

        // the response JSON from the OpenMRS does not contain full information
        // for
        // the provider. therefore, separate request(s) must be made to obtain
        // full provider
        // information. As an optimization, only make 1 request per unique
        // provider
        Map<String, org.motechproject.mrs.domain.Person> providers = new HashMap<>();
        for (Encounter encounter : result.getResults()) {
            providers.put(encounter.getProvider().getUuid(), null);
        }

        for (String providerUuid : providers.keySet()) {
            org.motechproject.mrs.domain.Person provider = personAdapter.getPerson(providerUuid);
            providers.put(providerUuid, provider);
        }

        List<org.motechproject.mrs.domain.Encounter> updatedEncounters = new ArrayList<>();
        for (Encounter encounter : result.getResults()) {
            org.motechproject.mrs.domain.Person person = providers.get(encounter.getProvider().getUuid());
            OpenMRSProvider provider = new OpenMRSProvider(person);
            provider.setProviderId(person.getPersonId());
            org.motechproject.mrs.domain.Encounter mrsEncounter = convertToMrsEncounter(encounter,
                    provider, patient);
            updatedEncounters.add(mrsEncounter);
        }

        return updatedEncounters;
    }

    private org.motechproject.mrs.domain.Encounter convertToMrsEncounter(Encounter encounter, org.motechproject.mrs.domain.Provider mrsPerson, org.motechproject.mrs.domain.Patient patient) {
        org.motechproject.mrs.domain.Encounter updated = new OpenMRSEncounter.MRSEncounterBuilder().withId(encounter.getUuid()).withProvider(mrsPerson)
                .withFacility(ConverterUtils.convertLocationToMrsLocation(encounter.getLocation()))
                .withDate(encounter.getEncounterDatetime()).withPatient(patient)
                .withObservations(convertToMrsObservation(encounter.getObs()))
                .withEncounterType(encounter.getEncounterType().getName()).build();

        return updated;
    }

    private Set<OpenMRSObservation> convertToMrsObservation(List<Observation> obs) {
        Set<OpenMRSObservation> mrsObs = new HashSet<OpenMRSObservation>();

        for (Observation ob : obs) {
            mrsObs.add(ConverterUtils.convertObservationToMrsObservation(ob));
        }

        return mrsObs;
    }

    @Override
    public org.motechproject.mrs.domain.Encounter getEncounterById(String id) {
        try {
            Encounter encounter = encounterResource.getEncounterById(id);
            org.motechproject.mrs.domain.Patient patient = (org.motechproject.mrs.domain.Patient) patientAdapter.getPatient(encounter.getPatient().getUuid());
            org.motechproject.mrs.domain.Person person = personAdapter.getPerson(encounter.getProvider().getUuid());
            org.motechproject.mrs.domain.Provider provider = new org.motechproject.openmrs.model.OpenMRSProvider(person);
            provider.setProviderId(person.getPersonId());
            return convertToMrsEncounter(encounter, provider, patient);
        } catch (HttpException e) {
            return null;
        }
    }

    @Override
    public List<org.motechproject.mrs.domain.Encounter> getEncountersByEncounterType(String motechId, String encounterType) {
        Validate.notEmpty(motechId, "MoTeCH Id cannot be empty");

        List<org.motechproject.mrs.domain.Encounter> previousEncounters = getAllEncountersByPatientMotechId(motechId);

        removeEncounters(previousEncounters, encounterType);

        return previousEncounters;
    }

    private void removeEncounters(List<org.motechproject.mrs.domain.Encounter> previousEncounters, String encounterType) {

        Iterator<org.motechproject.mrs.domain.Encounter> encounterItr = previousEncounters.iterator();

        // filter out encounters with non matching encounterType
        while (StringUtils.isNotBlank(encounterType) && encounterItr.hasNext()) {
            org.motechproject.mrs.domain.Encounter enc = encounterItr.next();
            if (!encounterType.equals(enc.getEncounterType())) {
                encounterItr.remove();
            }
        }
    }


}

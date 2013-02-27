package org.motechproject.mrs.helper;

import org.motechproject.mrs.domain.Attribute;
import org.motechproject.mrs.domain.Facility;
import org.motechproject.mrs.domain.Patient;
import org.motechproject.mrs.domain.Person;
import org.motechproject.mrs.model.AttributeDto;
import org.motechproject.mrs.model.FacilityDto;
import org.motechproject.mrs.model.PatientDto;
import org.motechproject.mrs.model.PersonDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PatientHelper {
    public PatientDto getPatientDto(Patient patient) {
        final PatientDto patientDto = new PatientDto();
        patientDto.setFacility(createFacility(patient.getFacility()));
        patientDto.setPerson(createPersonDto(patient.getPerson()));
        patientDto.setMotechId(patient.getMotechId());
        patientDto.setPatientId(patient.getPatientId());

        return patientDto;
    }

    public List<Attribute> getAttributesList(List<AttributeDto> attributesDto) {
        final List<Attribute> attributesList = new ArrayList<>();

        for (Attribute attribute : attributesDto) {
            AttributeDto attributeDto = new AttributeDto();
            attributeDto.setName(attribute.getName());
            attributeDto.setValue(attribute.getValue());

            attributesList.add(attributeDto);
        }

        return attributesList;
    }

    private Person createPersonDto(Person person) {
        final PersonDto personDto = new PersonDto();
        final List<Attribute> attributeList = new ArrayList<>();

        if (person != null) {
            for (Attribute attribute : person.getAttributes()) {
                AttributeDto attributeDto = new AttributeDto();
                attributeDto.setName(attribute.getName());
                attributeDto.setValue(attribute.getValue());

                attributeList.add(attributeDto);
            }

            personDto.setPersonId(person.getPersonId());
            personDto.setFirstName(person.getFirstName());
            personDto.setLastName(person.getLastName());
            personDto.setMiddleName(person.getMiddleName());
            personDto.setPreferredName(person.getPreferredName());
            personDto.setDateOfBirth(person.getDateOfBirth());
            personDto.setBirthDateEstimated(person.getBirthDateEstimated());
            personDto.setAddress(person.getAddress());
            personDto.setAge(person.getAge());
            personDto.setGender(person.getGender());
            personDto.setDead(person.isDead());
            personDto.setAttributes(attributeList);
            personDto.setDeathDate(person.getDeathDate());
        }
        return personDto;
    }

    private Facility createFacility(Facility facility) {
        final FacilityDto facilityDto = new FacilityDto();

        String facilityId = null;
        if (facility != null) {
            facilityId = facility.getFacilityId();
        }
        facilityDto.setFacilityId(facilityId);

        return facilityDto;
    }
}

package org.motechproject.mrs.helper;

import org.motechproject.mrs.domain.Attribute;
import org.motechproject.mrs.domain.Patient;
import org.motechproject.mrs.model.AttributeDto;
import org.motechproject.mrs.model.FacilityDto;
import org.motechproject.mrs.model.PatientDto;
import org.motechproject.mrs.model.PersonDto;

import java.util.ArrayList;
import java.util.List;

public class PatientHelper {
    private PatientHelper() {
        // static utility class
    }

    public static PatientDto getPatientDto(Patient patient) {
        String facilityId = null;
        if (patient.getFacility() != null) {
            facilityId = patient.getFacility().getFacilityId();
        }
        FacilityDto facilityDto = new FacilityDto();
        facilityDto.setFacilityId(facilityId);

        List<Attribute> attributeList = new ArrayList<>();

        if (patient.getPerson() != null) {
            for (Attribute attribute : patient.getPerson().getAttributes()) {
                AttributeDto attributeDto = new AttributeDto();
                attributeDto.setName(attribute.getName());
                attributeDto.setValue(attribute.getValue());

                attributeList.add(attributeDto);
            }
        }

        PersonDto personDto = null;
        if (patient.getPerson() != null) {
            personDto = new PersonDto();
            personDto.setPersonId(patient.getPerson().getPersonId());
            personDto.setFirstName(patient.getPerson().getFirstName());
            personDto.setLastName(patient.getPerson().getLastName());
            personDto.setMiddleName(patient.getPerson().getMiddleName());
            personDto.setPreferredName(patient.getPerson().getPreferredName());
            personDto.setDateOfBirth(patient.getPerson().getDateOfBirth());
            personDto.setBirthDateEstimated(patient.getPerson().getBirthDateEstimated());
            personDto.setAddress(patient.getPerson().getAddress());
            personDto.setAge(patient.getPerson().getAge());
            personDto.setGender(patient.getPerson().getGender());
            personDto.setDead(patient.getPerson().isDead());
            personDto.setAttributes(attributeList);
            personDto.setDeathDate(patient.getPerson().getDeathDate());
        }

        PatientDto patientDto = new PatientDto();
        patientDto.setFacility(facilityDto);
        patientDto.setPerson(personDto);
        patientDto.setMotechId(patient.getMotechId());
        patientDto.setPatientId(patient.getPatientId());

        return patientDto;
    }

    public static List<Attribute> getAttributesList(List<AttributeDto> attributesDto) {
        List<Attribute> attributesList = new ArrayList<>();

        for (Attribute attribute : attributesDto) {
            AttributeDto attributeDto = new AttributeDto();
            attributeDto.setName(attribute.getName());
            attributeDto.setValue(attribute.getValue());

            attributesList.add(attributeDto);
        }

        return attributesList;
    }
}

package org.motechproject.mrs.model;

import java.util.ArrayList;
import java.util.List;

public class ContainerDto {
    private PersonDto personDto;
    private PatientDto patientDto;
    private FacilityDto facilityDto;
    private List<AttributeDto> attributesDto = new ArrayList<>();

    public List<AttributeDto> getAttributesDto() {
        return attributesDto;
    }

    public void setAttributesDto(List<AttributeDto> attributes) {
        this.attributesDto = attributes;
    }

    public FacilityDto getFacilityDto() {
        return facilityDto;
    }

    public void setFacilityDto(FacilityDto facilityDto) {
        this.facilityDto = facilityDto;
    }

    public PersonDto getPersonDto() {
        return personDto;
    }

    public void setPersonDto(PersonDto personDto) {
        this.personDto = personDto;
    }

    public PatientDto getPatientDto() {
        return patientDto;
    }

    public void setPatientDto(PatientDto patientDto) {
        this.patientDto = patientDto;
    }
}

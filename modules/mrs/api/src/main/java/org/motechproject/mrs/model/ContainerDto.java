package org.motechproject.mrs.model;

import org.motechproject.mrs.model.PatientDto;
import org.motechproject.mrs.model.PersonDto;

public class ContainerDto {

    private PersonDto personDto;
    private PatientDto patientDto;
    private FacilityDto facilityDto;

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

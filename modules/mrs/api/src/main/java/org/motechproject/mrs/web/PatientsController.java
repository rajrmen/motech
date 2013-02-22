package org.motechproject.mrs.web;

import org.motechproject.mrs.domain.Patient;
import org.motechproject.mrs.helper.PatientHelper;
import org.motechproject.mrs.model.ContainerDto;
import org.motechproject.mrs.model.FacilityDto;
import org.motechproject.mrs.model.PatientDto;
import org.motechproject.mrs.model.PersonDto;
import org.motechproject.mrs.services.PatientAdapter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.http.HttpStatus;

import java.util.List;

@Controller
public class PatientsController {

    private List<PatientAdapter> patientAdapters;

    public void setPatientAdapters(List<PatientAdapter> patientAdapters) {
        this.patientAdapters = patientAdapters;
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/patients/save", method = RequestMethod.POST)
    public void save(@RequestBody ContainerDto containerDto) {
        FacilityDto facilityDto = containerDto.getFacilityDto();

        PersonDto person = containerDto.getPersonDto();
        person.setAttributes(PatientHelper.getAttributesList(containerDto.getAttributesDto()));

        PatientDto patient = containerDto.getPatientDto();
        patient.setPerson(person);
        patient.setFacility(facilityDto);

        patientAdapters.get(0).savePatient(patient);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/patients/update", method = RequestMethod.POST)
    public void update(@RequestBody ContainerDto containerDto) {
        FacilityDto facilityDto = containerDto.getFacilityDto();

        PersonDto person = containerDto.getPersonDto();
        person.setAttributes(PatientHelper.getAttributesList(containerDto.getAttributesDto()));

        PatientDto patient = containerDto.getPatientDto();
        patient.setPerson(person);
        patient.setFacility(facilityDto);

        patientAdapters.get(0).updatePatient(patient);
    }

    @RequestMapping(value = "/patients", method = RequestMethod.GET)
    @ResponseBody public List<Patient> getPatient() {
        return patientAdapters.get(0).getAllPatients();
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/patients/getPatient", method = RequestMethod.POST)
    @ResponseBody public Patient getPatient(@RequestBody String motechID) {
        return PatientHelper.getPatientDto(patientAdapters.get(0).getPatientByMotechId(motechID));
    }

    @RequestMapping(value = "/patients/{mrsId}", method = RequestMethod.GET)
    @ResponseBody public Patient getPatientByPath(@PathVariable String mrsId) {
        return PatientHelper.getPatientDto(patientAdapters.get(0).getPatientByMotechId(mrsId));
    }
}

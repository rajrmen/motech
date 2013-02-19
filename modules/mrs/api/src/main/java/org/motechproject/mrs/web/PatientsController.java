package org.motechproject.mrs.web;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.mrs.domain.Patient;
import org.motechproject.mrs.model.ContainerDto;
import org.motechproject.mrs.model.FacilityDto;
import org.motechproject.mrs.model.PatientDto;
import org.motechproject.mrs.model.PersonDto;
import org.motechproject.mrs.services.PatientAdapter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

import java.util.List;

@Controller
public class PatientsController {

    //private PatientAdapter patientAdapter;
    private List<PatientAdapter> patientAdapters;

    public void setPatientAdapters(List<PatientAdapter> patientAdapters) {
        this.patientAdapters = patientAdapters;
    }



   /* @Autowired
    public PatientsController(PatientAdapter patientAdapter){
        this.patientAdaptersList = patientAdapter;
    }
*/

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/patients/save", method = RequestMethod.POST)
    public void save(@RequestBody ContainerDto containerDto) {
        FacilityDto facilityDto = containerDto.getFacilityDto();
        PersonDto person = containerDto.getPersonDto();
        PatientDto patient = containerDto.getPatientDto();
        patient.setPerson(person);
        patient.setFacility(facilityDto);

        patientAdapters.get(0).savePatient(patient);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/patients/update", method = RequestMethod.POST)
    public void update(@RequestBody ContainerDto containerDto) {
        PersonDto person = containerDto.getPersonDto();
        PatientDto patient = containerDto.getPatientDto();
        patient.setPerson(person);

        patientAdapters.get(0).updatePatient(patient);
    }

    @RequestMapping(value = "/patients", method = RequestMethod.GET)
    @ResponseBody public List<Patient> getPatient() {
        List<Patient> patients = patientAdapters.get(0).getAllPatients();

        return patients;
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/patients/getPatient", method = RequestMethod.POST)
    @ResponseBody public Patient getPatient(@RequestBody String motechID) {
        return patientAdapters.get(0).getPatientByMotechId(motechID);
    }



}

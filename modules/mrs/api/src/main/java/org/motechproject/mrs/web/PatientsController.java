package org.motechproject.mrs.web;

import org.motechproject.mrs.domain.Patient;
import org.motechproject.mrs.exception.PatientNotFoundException;
import org.motechproject.mrs.helper.PatientHelper;
import org.motechproject.mrs.model.ContainerDto;
import org.motechproject.mrs.model.FacilityDto;
import org.motechproject.mrs.model.PatientDto;
import org.motechproject.mrs.model.PersonDto;
import org.motechproject.mrs.services.PatientAdapter;
import org.motechproject.mrs.util.DefaultPatientAdapter;
import org.motechproject.mrs.util.MrsImplementationsDataProvider;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class PatientsController {
    private DefaultPatientAdapter defaultPatientAdapter = new DefaultPatientAdapter();

/*    @Autowired
    private PatientHelper patientHelper;*/

    private PatientHelper patientHelper = new PatientHelper();

    @RequestMapping(value = "/patients", method = RequestMethod.GET)
    @ResponseBody
    public List<Patient> getPatient() {
        if (defaultPatientAdapter.getPatientAdapter() == null) {
            initializeDeafualtPatientAdapter();
        }
        return defaultPatientAdapter.getPatientAdapter().getAllPatients();
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/patients/get/patient", method = RequestMethod.POST)
    @ResponseBody public Patient getPatient(@RequestBody String motechID) throws PatientNotFoundException {
        try {
            return patientHelper.getPatientDto(defaultPatientAdapter.getPatientAdapter().getPatientByMotechId(motechID));
        } catch (Exception ex) {
            throw new PatientNotFoundException(ex);
        }
    }

    @RequestMapping(value = "/patients/{mrsId}", method = RequestMethod.GET)
    @ResponseBody
    public Patient getPatientByPath(@PathVariable String mrsId) {
        return patientHelper.getPatientDto(defaultPatientAdapter.getPatientAdapter().getPatientByMotechId(mrsId));
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/patients/save", method = RequestMethod.POST)
    public void save(@RequestBody ContainerDto containerDto) {
        FacilityDto facilityDto = containerDto.getFacilityDto();

        PersonDto person = containerDto.getPersonDto();
        person.setAttributes(patientHelper.getAttributesList(containerDto.getAttributesDto()));

        PatientDto patient = containerDto.getPatientDto();
        patient.setPerson(person);
        patient.setFacility(facilityDto);

        defaultPatientAdapter.getPatientAdapter().savePatient(patient);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/patients/update", method = RequestMethod.POST)
    public void update(@RequestBody ContainerDto containerDto) {
        FacilityDto facilityDto = containerDto.getFacilityDto();

        PersonDto person = containerDto.getPersonDto();
        person.setAttributes(patientHelper.getAttributesList(containerDto.getAttributesDto()));

        PatientDto patient = containerDto.getPatientDto();
        patient.setPerson(person);
        patient.setFacility(facilityDto);

        defaultPatientAdapter.getPatientAdapter().updatePatient(patient);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/patients/adapters/get/all", method = RequestMethod.POST)
    @ResponseBody public List<String> getPatientAdapterMap() {
        Map<String, PatientAdapter> map = MrsImplementationsDataProvider.getPatientAdapterMap();
        List<String> patientAdapterList = new ArrayList<>();
        for (String e : map.keySet()) {
            patientAdapterList.add(e);
        }
        return patientAdapterList;
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/patients/adapters/get/default", method = RequestMethod.POST)
    @ResponseBody public String getDefaultPatientAdapter() {
        return defaultPatientAdapter.getName();
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/patients/adapters/set", method = RequestMethod.POST)
    public void setActivePatientAdapter(@RequestBody String selectedItem) {
        defaultPatientAdapter.setName(selectedItem);
        defaultPatientAdapter.setPatientAdapter(MrsImplementationsDataProvider.getPatientAdapterMap().get(selectedItem));
    }

    private void initializeDeafualtPatientAdapter() {
        String name = MrsImplementationsDataProvider.getPatientAdapterMap().keySet().iterator().next();

        defaultPatientAdapter.setPatientAdapter(MrsImplementationsDataProvider.getPatientAdapterMap().get(name));
        defaultPatientAdapter.setName(name);
    }
}

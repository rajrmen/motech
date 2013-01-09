package org.motechproject.mrs;

import org.motechproject.commons.api.AbstractDataProviderLookup;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.services.MRSFacilityAdapter;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MRSDataProviderLookup extends AbstractDataProviderLookup {
    private static final String SUPPORT_FIELD = "id";

    private List<MRSPatientAdapter> patientAdapters;
    private List<MRSFacilityAdapter> facilityAdapters;

    @Autowired
    public MRSDataProviderLookup(ResourceLoader resourceLoader) {
        Resource resource = resourceLoader.getResource("task-data-provider.json");

        if (resource != null) {
            setBody(resource);
        }
    }

    @Override
    public Object lookup(String clazz, Map<String, String> lookupFields) {
        Object obj = null;

        if (supports(clazz) && lookupFields.containsKey(SUPPORT_FIELD)) {
            String id = lookupFields.get(SUPPORT_FIELD);

            if (MRSPatient.class.getName().equalsIgnoreCase(clazz)) {
                obj = getPatient(id);
            } else if (MRSFacility.class.getName().equalsIgnoreCase(clazz)) {
                obj = getFacility(id);
            }
        }

        return obj;
    }

    @Override
    public List<Class<?>> getSupportClasses() {
        return Arrays.asList(MRSPerson.class, MRSPatient.class, MRSFacility.class);
    }

    public void setPatientAdapters(List<MRSPatientAdapter> patientAdapters) {
        this.patientAdapters = patientAdapters;
    }

    public void setFacilityAdapters(List<MRSFacilityAdapter> facilityAdapters) {
        this.facilityAdapters = facilityAdapters;
    }

    private Object getPatient(String patientId) {
        Object obj = null;

        if (patientAdapters != null && !patientAdapters.isEmpty()) {
            for (MRSPatientAdapter adapter : patientAdapters) {
                obj = adapter.getPatient(patientId);
            }
        }

        return obj;
    }

    private MRSFacility getFacility(String facilityId) {
        MRSFacility facility = null;

        if (facilityAdapters != null && !facilityAdapters.isEmpty()) {
            for (MRSFacilityAdapter adapter : facilityAdapters) {
                facility = adapter.getFacility(facilityId);
            }
        }

        return facility;
    }
}
